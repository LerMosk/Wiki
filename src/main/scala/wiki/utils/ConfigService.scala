package wiki.utils

import java.io.FileInputStream
import java.util.Properties

object ConfigService {
  private val p = new Properties
  val stream = new FileInputStream("conf.d/server.properties")
  p.load(stream)
  stream.close()

  def getServerAddress: String = getStringParam("server_address")

  def getServerPort: Int = getIntParam("server_port")

  // ------------------------------------------------------------
  // Mongo
  // ------------------------------------------------------------

  def getMongoAuthRequired: Boolean = getBoolParam("mongo_auth_required")

  def getMongoAddress: String = getStringParam("mongo_address")

  def getMongoStandaloneHost: String = getStringParam("mongo_host")

  def getMongoDatabase: String = getStringParam("mongo_database")

  def getMongoUser: String = getStringParam("mongo_username")

  def getMongoPassword: String = getStringParam("mongo_password")


  // ------------------------------------------------------------
  // Utils
  // ------------------------------------------------------------

  private def getStringParam(paramName: String, defaultValue: String = ""): String = {
    val paramValue = p.getProperty(paramName)
    if (paramValue == null) defaultValue else paramValue
  }

  private def getIntParam(paramName: String, defaultValue: Int = 0): Int = {
    val paramValue = p.getProperty(paramName)
    if (paramValue == null) defaultValue else paramValue.toInt
  }

  private def getLongParam(paramName: String, defaultValue: Long = 0L): Long = {
    val paramValue = p.getProperty(paramName)
    if (paramValue == null) defaultValue else paramValue.toLong
  }

  private def getBoolParam(paramName: String, defaultValue: Boolean = false): Boolean = {
    val paramValue = p.getProperty(paramName)
    if (paramValue == null) defaultValue else paramValue.toBoolean
  }
}
