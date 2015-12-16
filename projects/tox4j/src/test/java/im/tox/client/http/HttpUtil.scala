package im.tox.client.http

import java.io.{OutputStreamWriter, PrintWriter}
import java.nio.charset.Charset

import com.sun.net.httpserver.HttpExchange
import com.trueaccord.scalapb.GeneratedMessage

case object HttpUtil {

  private val UTF_8 = Charset.forName("UTF-8")

  def sendText(exchange: HttpExchange)(write: PrintWriter => Unit): Unit = {
    exchange.getResponseHeaders.add("Content-Type", "text/plain; charset=utf-8")
    exchange.sendResponseHeaders(200, 0)

    val out = new PrintWriter(new OutputStreamWriter(exchange.getResponseBody))
    write(out)
    out.close()
  }

  def send(exchange: HttpExchange, response: GeneratedMessage): Unit = {
    exchange.getResponseHeaders.add("Content-Type", "application/octet-stream")
    exchange.sendResponseHeaders(200, response.serializedSize)

    val os = exchange.getResponseBody
    response.writeTo(os)
    os.close()
  }

}
