package wiki.routing

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{entity, _}
import akka.http.scaladsl.server.{Directive0, ExceptionHandler, Route}
import ch.qos.logback.classic.util.ContextInitializer
import org.json4s.Formats
import org.json4s.jackson.JsonMethods.parseOpt
import org.json4s.jackson.Serialization.writePretty
import org.slf4j.{Logger, LoggerFactory}
import wiki.db.MongoProvider
import wiki.models.{Catalog, Response, WikiArticle, WikiArticleUpd}
import wiki.utils.CatalogCreator
import wiki.utils.Utils.CustomDateSerializer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Routing {

  implicit val formats: Formats = org.json4s.DefaultFormats + CustomDateSerializer

  System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "conf.d/logback.xml")
  val logger: Logger = LoggerFactory.getLogger(getClass)

  val routing: Route = (handleExceptions(exceptionHandler) & respondWithJsonContentType) {
    pathPrefix("wiki") {
      (get & path("catalog")) {
        val result = MongoProvider.findCatalogItems() map CatalogCreator.createCatalog map Response.success map writeJson
        complete(result)

      } ~ (get & path(Segment) & parameter("pretty".?)) { (title, pretty) =>
        val result = MongoProvider.findArticle(title) map {
          case Some(art) => Response.success(art)
          case None => Response.failed(404, s"Can't find article $title")
        } map { res =>
          if (pretty.isDefined) writePretty(res) else writeJson(res)
        }
        complete(result)

      } ~ (post & entity(as[String])) { body =>
        val result = (readOpt[WikiArticle](body) match {
          case Some(article) => MongoProvider.insertArticle(article).map(_ => Response.success("Ok"))
          case None => Future.successful(Response.failed(400, "Invalid request"))
        }) map writeJson
        complete(result)

      } ~ (put & entity(as[String])) { body =>
        val result = (readOpt[WikiArticleUpd](body) match {
          case Some(articleUpd) => MongoProvider.updateArticle(articleUpd).map(_ => Response.success("Ok"))
          case _ => Future.successful(Response.failed(400, "Invalid request"))
        }) map writeJson
        complete(result)

      }
    }
  }

  protected def readOpt[T: Manifest](jsonString: String): Option[T] = {
    parseOpt(jsonString).flatMap(_.extractOpt[T])
  }

  protected def writeJson(obj: AnyRef): String = org.json4s.jackson.Serialization.write(obj)

  private def respondWithJsonContentType: Directive0 =
    mapResponse(response => response.mapEntity(entity => entity.withContentType(ContentTypes.`application/json`)))

  protected def exceptionHandler: ExceptionHandler = {
    ExceptionHandler.apply {
      case ex: Exception =>
        extractMatchedPath { uri =>
          logger.error(s"Request to $uri exception:", ex)
          val entity = HttpEntity(ContentTypes.`application/json`, writeJson(Response.failed(500, "Internal server error")))
          complete(HttpResponse(StatusCodes.InternalServerError, entity = entity))
        }
    }
  }
}
