package wiki.models

case class Response(success: Boolean, result: Any, t: Long = System.currentTimeMillis())

case class Error(code: Int, description: String)

object Response {
  def failed(code: Int, text: String): Response = {
    Response(success = false, Error(code, text))
  }

  def success(result: Any): Response = {
    Response(success = true, result)
  }
}
