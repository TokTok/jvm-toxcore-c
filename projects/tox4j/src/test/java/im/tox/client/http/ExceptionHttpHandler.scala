package im.tox.client.http

import java.io.{PrintWriter, StringWriter}

import com.sun.net.httpserver.{HttpExchange, HttpHandler}

import scala.util.control.NonFatal

final case class ExceptionHttpHandler(inner: HttpHandler) extends HttpHandler {

  override def handle(exchange: HttpExchange): Unit = {
    try {
      inner.handle(exchange)
    } catch {
      case NonFatal(exception) =>
        val response = new StringWriter

        val out = new PrintWriter(response)
        exception.printStackTrace(out)
        out.close()

        HttpUtil.send(exchange, response.toString)
    }
  }

}
