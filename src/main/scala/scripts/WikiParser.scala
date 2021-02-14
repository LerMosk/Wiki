package scripts

import org.json4s.{DefaultFormats, Formats}
import org.slf4j.{Logger, LoggerFactory}
import wiki.db.MongoProvider
import wiki.models.WikiArticle
import wiki.utils.Utils.readOpt

import java.io.FileInputStream
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.io.Source.fromInputStream
import scala.language.postfixOps

object WikiParser {
  implicit val format: Formats = DefaultFormats
  val log: Logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    val inputFile = args(0)
    val articles = fromInputStream(new FileInputStream(inputFile)).getLines() map readOpt[WikiArticle] collect {
      case Some(article) => article
    } toList

    Await.ready(MongoProvider.insertArticles(articles), 5 minutes)
  }

}
