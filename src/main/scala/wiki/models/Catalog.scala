package wiki.models

import org.bson.types.ObjectId
import org.mongodb.scala.bson.annotations.BsonProperty


case class Catalog(
                   @BsonProperty("_id") name: String,
                   sub: List[Catalog]
                   )

case class CatalogItem(
                    title: String,
                    category: List[String]
                  )
