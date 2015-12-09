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

  def run(ioDht: CoreError \/ IO[Dht]): State[Dht, CoreError \/ Seq[IO.Action]] = {
    ioDht match {
      case error @ -\/(failure) =>
        State.state(error)
      case \/-(ioDht) =>
        val (actions, dht) = ioDht.run(Nil)
        for {
          _ <- State.put(dht)
        } yield {
          \/-(actions)
        }
    }
  }

  def processNetworkEvent(packet: Packet): State[Dht, CoreError \/ Seq[IO.Action]] = {
    logger.debug("Processing network event: " + packet)

    for {
      dht <- State.get[Dht]
      actions <- run(ToxHandler(dht, packet.origin, PlainText(packet.bytes)))
    } yield {
      actions
    }
  }

  def processTimedActionEvent(action: Dht => CoreError \/ IO[Dht]): State[Dht, CoreError \/ Seq[IO.Action]] = {
    logger.debug("Processing timed action event")

    for {
      dht <- State.get[Dht]
      actions <- run(action(dht))
    } yield {
      actions
    }
  }

  def processShutdownEvent: State[Dht, CoreError \/ Seq[IO.Action]] = {
    logger.debug("Shutdown event")

    State.state(\/-(Seq(IO.Action.Shutdown)))
  }

  def processEvent(event: IO.Event): State[Dht, CoreError \/ Seq[IO.Action]] = {
    event match {
      case IO.Event.Network(packet) =>
        processNetworkEvent(packet)
      case IO.Event.TimedAction(action) =>
        processTimedActionEvent(action)
      case IO.Event.Shutdown =>
        processShutdownEvent
    }
  }

}
