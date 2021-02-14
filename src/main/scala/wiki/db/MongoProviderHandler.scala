package wiki.db

import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.conversions
import org.mongodb.scala.model.Updates.set
import org.mongodb.scala.{MongoClient, MongoClientSettings, MongoCredential, MongoDatabase, ServerAddress}
import org.slf4j.{Logger, LoggerFactory}
import wiki.models.{Catalog, CatalogItem, WikiArticle}
import wiki.utils.ConfigService
import wiki.utils.Utils.MSA

import scala.collection.JavaConverters._

trait MongoProviderHandler {

  protected val log: Logger = LoggerFactory.getLogger(getClass)

  protected val codecRegistry: CodecRegistry = fromRegistries(fromProviders(
    classOf[WikiArticle],
    classOf[CatalogItem]
    ),
    DEFAULT_CODEC_REGISTRY)

  protected val db: MongoDatabase = {
    val user: String = ConfigService.getMongoUser
    val source: String = ConfigService.getMongoDatabase
    val password: Array[Char] = ConfigService.getMongoPassword.toCharArray
    val credential: MongoCredential = MongoCredential.createCredential(user, source, password)

    val settings: MongoClientSettings = MongoClientSettings.builder()
      .applyToClusterSettings(b => b.hosts(List(new ServerAddress(ConfigService.getMongoStandaloneHost)).asJava))
      .credential(credential)
      .build()

    val mongoClient =
      if (ConfigService.getMongoAuthRequired)
        MongoClient(settings)
      else MongoClient(ConfigService.getMongoAddress)

    mongoClient.getDatabase(source).withCodecRegistry(codecRegistry)
  }

  protected def msaToUpd(msa: MSA): Seq[conversions.Bson] = {
    msa.map{case (k, v) => set(k, v)}.toSeq
  }
}
