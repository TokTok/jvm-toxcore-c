package im.tox.core.network

import com.typesafe.scalalogging.Logger
import im.tox.core.crypto.PlainText
import im.tox.core.dht.Dht
import im.tox.core.error.CoreError
import im.tox.core.io.IO
import org.slf4j.LoggerFactory

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

  def processTimedActionEvent(action: Dht => IO[Dht]): State[Dht, CoreError \/ Seq[IO.Action]] = {
    logger.debug("Processing time event")

    for {
      dht <- State.get[Dht]
      actions <- {
        val (actions, newDht) = action(dht).run(Nil)
        for {
          _ <- State.put(newDht)
        } yield {
          actions
        }
      }
    } yield {
      \/-(actions)
    }
  }

  def processShutdownEvent: State[Dht, CoreError \/ Seq[IO.Action]] = {
    logger.debug("Shutdown event")

    State(dht => (dht, \/-(Seq(IO.Action.Shutdown))))
  }

  def processEvent(event: IO.Event): State[Dht, CoreError \/ Seq[IO.Action]] = {
    event match {
      case IO.NetworkEvent(packet) =>
        processNetworkEvent(packet)
      case IO.TimedActionEvent(action) =>
        processTimedActionEvent(action)
      case IO.ShutdownEvent =>
        processShutdownEvent
    }
  }

}
