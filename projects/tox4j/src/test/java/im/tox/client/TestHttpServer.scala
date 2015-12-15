package im.tox.client

import java.io.{PrintWriter, StringWriter}
import java.net.InetSocketAddress
import java.nio.charset.Charset

import codes.reactive.scalatime.Instant
import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}
import com.typesafe.scalalogging.Logger
import im.tox.core.network.Port
import im.tox.tox4j.impl.jni.ToxJniLog
import im.tox.tox4j.impl.jni.proto.{JniLog, JniLogEntry}
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.util.control.NonFatal

final class TestHttpServer(port: Port) {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  private var jniLog: Seq[JniLogEntry] = Nil
  private var state: List[ToxClient] = Nil

  // About 1 second of updates.
  private val maxLastUpdates = 20
  private val lastUpdates: mutable.Queue[Long] = new mutable.Queue[Long]

  private val startTime = Instant()
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

  def update(clients: List[ToxClient]): Unit = {
    lastUpdates.enqueue(System.currentTimeMillis())
    if (lastUpdates.length > maxLastUpdates) {
      lastUpdates.dequeue()
    }

    state = clients

    jniLog ++= filterIteration(ToxJniLog().entries)

    jniLog match {
      case head +: tail => jniLog = head +: tail.takeRight(ToxJniLog.maxSize)
      case _            => // No entries.
    }
  }

  private def send(exchange: HttpExchange, response: String): Unit = {
    val bytes = response.getBytes(UTF_8)

    exchange.getResponseHeaders.add("Content-Type", "text/plain; charset=utf-8")
    exchange.sendResponseHeaders(200, bytes.length)

    val os = exchange.getResponseBody
    os.write(bytes)
    os.close()
  }

  object RootHandler extends HttpHandler {

    override def handle(exchange: HttpExchange): Unit = {
      val state = TestHttpServer.this.state
      val response = new StringWriter
      val out = new PrintWriter(response)

      out.println(s"$Main running ${state.length} Tox instances, started on $startTime.")
      out.println()

      val averageUpdateTime = {
        val times = lastUpdates.zip(lastUpdates.tail).map { case (prev, next) => next - prev }
        times.sum / times.length
      }
      out.println(s"Average time between iterations: ${averageUpdateTime}ms (${1000 / averageUpdateTime} updates / second)")
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

      out.close()

      send(exchange, response.toString)
    }

  }

  case object ProfileBinaryHandler extends HttpHandler {

    override def handle(exchange: HttpExchange): Unit = {
      exchange.getResponseHeaders.add("Content-Type", "application/octet-stream")
      exchange.sendResponseHeaders(200, 0)

      val os = exchange.getResponseBody
      state.foreach(_.state.profile.writeTo(os))
      os.close()
    }

  }

  case object ProfileTextHandler extends HttpHandler {

    override def handle(exchange: HttpExchange): Unit = {
      val result = state.map(_.state.profile.toString).mkString("\n").getBytes(UTF_8)

      exchange.getResponseHeaders.add("Content-Type", "text/plain; charset=utf-8")
      exchange.sendResponseHeaders(200, result.length)

      val os = exchange.getResponseBody
      os.write(result)
      os.close()
    }

  }

  final case class ExceptionHandler(inner: HttpHandler) extends HttpHandler {
    override def handle(exchange: HttpExchange): Unit = {
      try {
        inner.handle(exchange)
      } catch {
        case NonFatal(exception) =>
          val response = new StringWriter

          val out = new PrintWriter(response)
          exception.printStackTrace(out)
          out.close()

          send(exchange, response.toString)
      }
    }
  }

  logger.info(s"Starting HTTP server on $port")
  val server = HttpServer.create(new InetSocketAddress(port.value), 0)
  server.createContext("/", ExceptionHandler(RootHandler))
  server.createContext("/profile", ExceptionHandler(ProfileBinaryHandler))
  server.createContext("/profile.txt", ExceptionHandler(ProfileTextHandler))
  server.setExecutor(null)
  server.start()

}
