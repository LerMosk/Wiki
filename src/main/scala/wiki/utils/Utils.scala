package wiki.utils

import org.bson.types.ObjectId
import org.json4s.JsonAST.{JInt, JLong, JString}
import org.json4s.{CustomSerializer, DefaultFormats, Formats}
import org.json4s.jackson.JsonMethods.parseOpt

import java.util.Date

object Utils {
  implicit val format: Formats = DefaultFormats
  type MSA = Map[String, Any]
  def readOpt[T: Manifest](jsonString: String): Option[T] = {
    parseOpt(jsonString).flatMap(_.extractOpt[T])
  }

  def genId(): String = new ObjectId().toHexString

  case object CustomDateSerializer
    extends CustomSerializer[Date](_ => ( {
      case JLong(i) => new Date(i)
      case JInt(i) => new Date(i.longValue())
    }, {
      case date: Date => JLong(date.toInstant.toEpochMilli)
    }))

  implicit class MsaHelper(msa: MSA) {
    def addOpt[T](name: String, opt: Option[T]): MSA = {
      msa ++ opt.map(v => name -> v)
    }
  }
}
