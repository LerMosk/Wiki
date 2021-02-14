package wiki

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import org.slf4j.{Logger, LoggerFactory}
import wiki.db.MongoProvider
import wiki.routing.Routing
import wiki.utils.ConfigService

import scala.concurrent.ExecutionContextExecutor

object ApiBoot extends Routing {
  override val logger: Logger = LoggerFactory.getLogger(getClass)
  implicit val system: ActorSystem = ActorSystem("Wiki")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def main(args: Array[String]): Unit = {
    MongoProvider.createIndexes()

    val address = ConfigService.getServerAddress
    val port = ConfigService.getServerPort

    logger.info(s"Binding application to $address:$port")
    Http().bindAndHandle(routing, address, port)

  }
}
