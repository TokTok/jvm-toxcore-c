package im.tox.client

import java.io.{PrintWriter, StringWriter}
import java.net.InetSocketAddress
import java.nio.charset.Charset

import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}
import com.typesafe.scalalogging.Logger
import im.tox.core.network.Port
import im.tox.tox4j.impl.jni.ToxJniLog
import im.tox.tox4j.impl.jni.proto.{JniLogEntry, JniLog}
import org.slf4j.LoggerFactory

import scala.util.Random

object TestHttpServer {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  private var jniLog: Seq[JniLogEntry] = Nil
  private var state: List[TestClient] = Nil

  private val UTF_8 = Charset.forName("UTF-8")

  private def filterIteration(entries: Seq[JniLogEntry]): Seq[JniLogEntry] = {
    entries.filterNot { entry =>
      Seq(
        "tox_iterate",
        "toxav_iterate",
        "tox_iteration_interval",
        "toxav_iteration_interval"
      ).contains(entry.name)
    }
  }

  def update(clients: List[TestClient]): Unit = {
    state = clients

    jniLog ++= filterIteration(ToxJniLog().entries)

    jniLog match {
      case head +: tail => jniLog = head +: tail.takeRight(ToxJniLog.maxSize)
      case _            => // No entries.
    }
  }

  object RootHandler extends HttpHandler {

    private def recentToxJniLog(): JniLog = {
      JniLog(jniLog)
    }

    private def send(exchange: HttpExchange, response: String): Unit = {
      val bytes = response.getBytes(UTF_8)

      exchange.getResponseHeaders.add("Content-Type", "text/plain; charset=utf-8")
      exchange.sendResponseHeaders(200, bytes.length)

      val os = exchange.getResponseBody
      os.write(bytes)
      os.close()
    }

    override def handle(exchange: HttpExchange): Unit = {
      val state = TestHttpServer.state
      val response = new StringWriter
      val out = new PrintWriter(response)

      try {
        out.println(s"$TestClient running ${state.length} Tox instances.")
        out.println()
        for (client <- state) {
          out.println(s"Instance ${client.tox.getAddress}:")
          out.println("  Friends:")
          for ((friendNumber, friend) <- client.state.friends) {
            out.println(s"    $friendNumber. $friend")
          }
          out.println()
        }

        out.println(s"Recent $ToxJniLog:")
        out.println(ToxJniLog.toString(JniLog(jniLog)))
      } finally {
        out.close()
      }

      send(exchange, response.toString)
    }

  }

  case object ProfileHandler extends HttpHandler {

    override def handle(exchange: HttpExchange): Unit = {
      exchange.getResponseHeaders.add("Content-Type", "application/octet-stream")
      exchange.sendResponseHeaders(200, 0)

      val os = exchange.getResponseBody
      state.foreach(_.state.profile.writeTo(os))
      os.close()
    }

  }

  def start(port: Port): Unit = {
    logger.info(s"Starting HTTP server on $port")
    val server = HttpServer.create(new InetSocketAddress(port.value), 0)
    server.createContext("/", RootHandler)
    server.createContext("/profile", ProfileHandler)
    server.setExecutor(null)
    server.start()
  }

}
