package wiki.models

import wiki.utils.Utils._

import java.util.Date

case class WikiArticle(
                       create_timestamp: Date,
                       timestamp: Date,
                       language: String,
                       wiki: String,
                       category: List[String],
                       title: String,
                       auxiliary_text: List[String]
                     )

case class WikiArticleUpd(
                           title: String,
                           category: Option[List[String]],
                           titleNew: Option[String],
                           auxiliary_text: Option[List[String]]
                         ) {
  def toMap: MSA = {
    Map(
      "category" -> category,
      "title" -> titleNew,
      "auxiliary_text" -> auxiliary_text,
      "timestamp" -> new Date()
    ) collect {
      case (k, Some(v)) => k -> v
    }
  }
}
