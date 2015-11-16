package im.tox.core.network

import com.typesafe.scalalogging.Logger
import im.tox.core.crypto.PlainText
import im.tox.core.dht.Dht
import im.tox.core.error.CoreError
import im.tox.core.io.IO
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.language.postfixOps
import scalaz._
import scalaz.stream.udp.Packet

object EventProcessor {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  def processNetworkEvent(packet: Packet): State[Dht, CoreError \/ Seq[IO.Action]] = {
    logger.debug("Network event: " + packet)

    for {
      result <- {
        for {
          dht <- State.get[Dht]
        } yield {
          ToxHandler(dht, packet.origin, PlainText(packet.bytes)) match {
            case error @ -\/(_) =>
              (error, dht)
            case \/-(ioDht) =>
              val (actions, dht) = ioDht.run(Nil)
              (\/-(actions), dht)
          }
        }
      }
      _ <- State.put(result._2)
    } yield {
      result._1
    }
  }

  def processTimingEvent(duration: Duration): State[Dht, CoreError \/ Seq[IO.Action]] = {
    logger.debug("Time event: " + duration.toSeconds)

    State(dht => (dht, \/-(Nil)))
  }

  def processShutdownEvent: State[Dht, CoreError \/ Seq[IO.Action]] = {
    logger.debug("Shutdown event")

    State(dht => (dht, \/-(Seq(IO.Action.Shutdown))))
  }

  def processEvent(event: IO.Event): State[Dht, CoreError \/ Seq[IO.Action]] = {
    event match {
      case IO.NetworkEvent(packet) =>
        processNetworkEvent(packet)
      case IO.TimeEvent(duration) =>
        processTimingEvent(duration)
      case IO.ShutdownEvent =>
        processShutdownEvent
    }
  }

}
