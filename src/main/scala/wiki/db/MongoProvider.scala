package wiki.db

import com.mongodb.client.model.CollationStrength
import org.bson.conversions.Bson
import org.mongodb.scala._
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.{Collation, Filters, IndexOptions, Indexes}
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates.{combine, set}
import wiki.models.{Catalog, CatalogItem, WikiArticle, WikiArticleUpd}

import java.util.Date
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MongoProvider extends MongoProviderHandler {

  private val wikiArticles: MongoCollection[WikiArticle] = db.getCollection("wiki_article")
  private val catalog: MongoCollection[CatalogItem] = db.getCollection("wiki_article")
  val caseInsensitiveCollation: Collation =
    Collation.builder().locale("ru").collationStrength(CollationStrength.PRIMARY).build()

  def createIndexes(): Unit = {
    createIndex(
      wikiArticles,
      Indexes.ascending("title"),
      IndexOptions().collation(caseInsensitiveCollation)
    )
  }

  private def createIndex[B](collection: MongoCollection[B], indexRule: Bson, options: IndexOptions = IndexOptions()): Future[Unit] = {
    log.info(s"Create indexes if not exists ${indexRule.toBsonDocument(BsonDocument.getClass, MongoClient.DEFAULT_CODEC_REGISTRY).toJson} on ${collection.namespace.getCollectionName}: started")
    collection.createIndex(indexRule, options.background(true)).toFuture map { _ =>
      log.info(s"Create indexes if not exists ${indexRule.toString} on ${collection.namespace.getCollectionName}: finished")
    }
  }

  def insertArticles(articles: Seq[WikiArticle]): Future[Any] =
    wikiArticles.insertMany(articles).toFuture() recover { case ex: Throwable =>
      log.error("Upload wiki articles in db error:", ex)
    }

  def insertArticle(article: WikiArticle): Future[Any] =
    wikiArticles.insertOne(article).toFuture()

  def updateArticle(articleUpd: WikiArticleUpd): Future[Any] = {
    val upd = combine(msaToUpd(articleUpd.toMap): _*)
    wikiArticles.updateOne(equal("title", articleUpd.title), upd).toFuture()
  }

  def findArticle(title: String): Future[Option[WikiArticle]] = {
    wikiArticles.find(equal("title", title)).collation(caseInsensitiveCollation).headOption() recover {
      case ex: Throwable =>
        log.error(s"Error find article $title", ex)
        None
    }
  }

  def findCatalogItems(): Future[Seq[CatalogItem]] = {
    catalog.find().toFuture() recover {
      case ex: Throwable =>
        log.error(s"Error find articles", ex)
        Seq.empty
    }
  }
}