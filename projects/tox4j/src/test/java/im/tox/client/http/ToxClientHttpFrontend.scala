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

import scala.collection.mutable

final class ToxClientHttpFrontend(port: Port) {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  private var jniLog: Seq[JniLogEntry] = Nil
  private var state: List[ToxClient] = Nil

  // About 1 second of updates.
  private val maxLastUpdates = 20
  private val lastUpdates = new mutable.Queue[Long]

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

  private case object StatusHandler extends HttpHandler {

    override def handle(exchange: HttpExchange): Unit = {
      val state = ToxClientHttpFrontend.this.state
      val response = new StringWriter
      val out = new PrintWriter(response)

      out.println(s"$TestClient running ${state.length} Tox instances, started on $startTime.")
      out.println()

      val averageUpdateTime = {
        val times = lastUpdates.zip(lastUpdates.tail).map { case (prev, next) => next - prev }
        times.sum / times.length
      }
      out.println(s"Average time between iterations: ${averageUpdateTime}ms (${1000 / averageUpdateTime} updates / second)")
      out.println()

      for ((client, id) <- state.zipWithIndex) {
        out.println(s"Instance $id:")
        out.println(s"  Friend Address: ${client.state.address}")
        out.println(s"  DHT Public Key: ${client.state.dhtId}")
        out.println(s"  UDP Port: ${client.state.udpPort}")
        out.println(s"  IPv4: ${HostInfo.ipv4}")
        out.println(s"  IPv6: ${HostInfo.ipv6}")
        out.println("  Friends:")
        for ((friendNumber, friend) <- client.state.friends) {
          out.println(s"    $friendNumber. $friend")
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

      HttpUtil.send(exchange, client.state.profile)
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
