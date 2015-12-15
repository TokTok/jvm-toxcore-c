package im.tox.client.http

import java.nio.charset.Charset

import com.sun.net.httpserver.HttpExchange
import com.trueaccord.scalapb.GeneratedMessage

case object HttpUtil {

  private val UTF_8 = Charset.forName("UTF-8")

  def send(exchange: HttpExchange, response: String): Unit = {
    val bytes = response.getBytes(UTF_8)

    exchange.getResponseHeaders.add("Content-Type", "text/plain; charset=utf-8")
    exchange.sendResponseHeaders(200, bytes.length)

    val os = exchange.getResponseBody
    os.write(bytes)
    os.close()
  }

  def send(exchange: HttpExchange, response: GeneratedMessage): Unit = {
    exchange.getResponseHeaders.add("Content-Type", "application/octet-stream")
    exchange.sendResponseHeaders(200, 0)

    val os = exchange.getResponseBody
    response.writeTo(os)
    os.close()
  }

}
