package im.tox.core.network

import com.typesafe.scalalogging.Logger
import im.tox.core.crypto.PlainText
import im.tox.core.dht.Dht
import im.tox.core.io.IO
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.language.postfixOps
import scalaz.stream._
import scalaz.stream.udp.Packet
import scalaz.{-\/, State, \/-}

@SuppressWarnings(Array(
  "org.brianmckenna.wartremover.warts.Any",
  "org.brianmckenna.wartremover.warts.AsInstanceOf",
  "org.brianmckenna.wartremover.warts.IsInstanceOf"
)) // scalastyle:off
object IoHandler {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  trait Event
  final case class TimeEvent(duration: Duration) extends Event
  final case class NetworkEvent(packet: Packet) extends Event

  type DhtState[A] = State[Dht, A]

  def processNetworkEvent(dht: Dht, packet: Packet): Process[DhtState, IO.Action] = {
    logger.debug("Network event: " + packet)

    val result =
      for {
        dht <- ToxHandler(dht, packet.origin, PlainText(packet.bytes))
      } yield {
        Process.await[DhtState, Unit, IO.Action](State.put(dht.value)) { _ =>
          Process.emitAll(dht.actions)
        }
      }

    result match {
      case -\/(error) =>
        logger.error(s"Fail: $error on $packet", error.exception)
        Process.empty
      case \/-(dht) =>
        dht
    }
  }

  def processTimingEvent(dht: Dht, duration: Duration): Process[DhtState, IO.Action] = {
    logger.debug("Time event: " + duration.toSeconds)
    Process.empty
  }

  def processEvent(dht: Dht, event: Event): Process[DhtState, IO.Action] = {
    event match {
      case NetworkEvent(packet) =>
        processNetworkEvent(dht, packet)
      case TimeEvent(duration) =>
        processTimingEvent(dht, duration)
    }
  }

}
