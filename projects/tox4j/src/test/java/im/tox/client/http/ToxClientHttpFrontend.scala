package im.tox.client.http

import java.net.InetSocketAddress

import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}
import com.typesafe.scalalogging.Logger
import im.tox.client.{HostInfo, ToxClient}
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

  private def fetchJniLog(): Unit = {
    jniLog ++= ToxJniLog().entries

    jniLog match {
      case head +: tail => jniLog = head +: tail.takeRight(ToxJniLog.maxSize)
      case _            => // No entries.
    }
  }

  def update(clients: List[ToxClient]): Unit = {
    loadAverage.update()

    state = clients

    if (ToxJniLog.size >= ToxJniLog.maxSize / 2) {
      logger.debug(s"Fetching JNI log (${ToxJniLog.size} >= ${ToxJniLog.maxSize} / 2)")
      fetchJniLog()
    }
  }

  private case object StatusHandler extends HttpHandler {

    override def handle(exchange: HttpExchange): Unit = {
      HttpUtil.sendText(exchange) { out =>
        val state = ToxClientHttpFrontend.this.state

        HostInfo.printSystemInfo(out)
        out.println()

        loadAverage.print(out)
        out.println()

        for ((client, id) <- state.zipWithIndex) {
          client.printInfo(out, id)
          out.println()
        }

        // Force an update before serving it to the user.
        fetchJniLog()
        out.println(s"Recent $ToxJniLog:")
        ToxJniLog.print(JniLog(jniLog))(out)
      }
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
      HttpUtil.sendText(exchange) { out =>
        for ((client, id) <- state.zipWithIndex) {
          out.println(s"Client $id")
          out.println(client.state.profile)
        }
      }
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
