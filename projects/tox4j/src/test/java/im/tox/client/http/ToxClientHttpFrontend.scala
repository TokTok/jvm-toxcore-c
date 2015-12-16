package im.tox.client.http

import java.io.{PrintWriter, StringWriter}
import java.net.InetSocketAddress

import codes.reactive.scalatime.Instant
import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}
import com.typesafe.scalalogging.Logger
import im.tox.client.{HostInfo, TestClient, ToxClient}
import im.tox.core.network.Port
import im.tox.tox4j.impl.jni.ToxJniLog
import im.tox.tox4j.impl.jni.proto.{JniLog, JniLogEntry}
import org.slf4j.LoggerFactory

final class ToxClientHttpFrontend(port: Port) {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  private var jniLog: Seq[JniLogEntry] = Nil
  private var state: List[ToxClient] = Nil

  /**
   * Expected average iterations per second when Tox is idling.
   */
  private val iterationsPerSecond = 1000 / 50
  private val loadAverage = new LoadAverage(iterationsPerSecond)

  private val startTime = Instant()

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
    loadAverage.update()

    state = clients

    jniLog ++= filterIteration(ToxJniLog().entries)

    jniLog match {
      case head +: tail => jniLog = head +: tail.takeRight(ToxJniLog.maxSize)
      case _            => // No entries.
    }
  }

  private case object StatusHandler extends HttpHandler {

    override def handle(exchange: HttpExchange): Unit = {
      val state = ToxClientHttpFrontend.this.state
      val response = new StringWriter
      val out = new PrintWriter(response)

      out.println(s"$TestClient running ${state.length} Tox instances, started on $startTime.")
      out.println()

      loadAverage.print(out)
      out.println()

      for ((client, id) <- state.zipWithIndex) {
        out.println(s"Instance $id (connection = ${client.state.connection}):")
        out.println(s"  Name:           ${client.state.profile.name}")
        out.println(s"  Status message: ${client.state.profile.statusMessage}")
        out.println(s"  Status:         ${client.state.profile.status}")
        out.println(s"  Friend address: ${client.state.address}")
        out.println(s"  DHT public key: ${client.state.dhtId}")
        out.println(s"  UDP port:       ${client.state.udpPort}")
        out.println(s"  IPv4 address:   ${HostInfo.ipv4}")
        out.println(s"  IPv6 address:   ${HostInfo.ipv6}")
        out.println("  Friends:")
        for ((friendNumber, friend) <- client.state.friends) {
          out.println(s"    $friendNumber -> $friend")
        }
        out.println()
      }

      out.println(s"Recent $ToxJniLog:")
      out.println(ToxJniLog.toString(JniLog(jniLog)))

      out.close()

      HttpUtil.send(exchange, response.toString)
    }

  }

  private case object ProfileBinaryHandler extends HttpHandler {

    private val ClientNumber = "(\\d+)".r

    override def handle(exchange: HttpExchange): Unit = {
      val client =
        exchange.getRequestURI.getQuery match {
          case ClientNumber(index) => state(index.toInt)
          case _                   => state.head
        }

      HttpUtil.send(
        exchange,
        client.state.profile
          // Don't save the name/status message in the web-save,
          // as they are set on startup.
          .withName("")
          .withStatusMessage("")
      )
    }

  }

  private case object ProfileTextHandler extends HttpHandler {

    override def handle(exchange: HttpExchange): Unit = {
      val result = state.map(_.state.profile.toString).mkString("\n")

      HttpUtil.send(exchange, result)
    }

  }

  logger.info(s"Starting HTTP server on $port")
  val server = HttpServer.create(new InetSocketAddress(port.value), 0)
  server.createContext("/", ExceptionHttpHandler(StatusHandler))
  server.createContext("/profile", ExceptionHttpHandler(ProfileBinaryHandler))
  server.createContext("/profile.txt", ExceptionHttpHandler(ProfileTextHandler))
  server.setExecutor(null)
  server.start()

}
