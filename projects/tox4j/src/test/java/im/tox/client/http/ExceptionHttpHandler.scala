package im.tox.client.http

import com.sun.net.httpserver.{HttpExchange, HttpHandler}

import scala.util.control.NonFatal

final case class ExceptionHttpHandler(inner: HttpHandler) extends HttpHandler {

  override def handle(exchange: HttpExchange): Unit = {
    try {
      inner.handle(exchange)
    } catch {
      case NonFatal(exception) =>
        HttpUtil.sendText(exchange) { out =>
          exception.printStackTrace(out)
        }
    }
  }

}
