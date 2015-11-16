package im.tox.core.io

import com.typesafe.scalalogging.Logger
import im.tox.core.crypto.PlainText.Conversions._
import im.tox.core.network.packets.ToxPacket
import im.tox.tox4j.core.ToxCoreConstants
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.language.postfixOps
import scalaz.concurrent.Task
import scalaz.stream._
import scalaz.{-\/, \/-}

@SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Any")) // scalastyle:off magic.number
object UdpActor {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  /**
   * Things of note in this module are the maximum UDP packet size define
   * (MAX_UDP_PACKET_SIZE) which sets the maximum UDP packet size toxcore can send
   * and receive.
   */
  private val MaxUdpPacketSize = 2048

  def make(
    actionSource: Process[Task, IO.Action],
    eventSink: Sink[Task, IO.Event]
  ): Process[Task, Unit] = {
    udp.listen(ToxCoreConstants.DefaultStartPort - 1) {
      import udp.syntax._

      val receiver = udp.receives(MaxUdpPacketSize, Some(6 seconds))
        .take(5)
        .map(IO.NetworkEvent)
        .to(eventSink)

      val sender = udp.lift(actionSource).flatMap {
        case IO.Action.SendTo(node, outPacket) =>
          logger.debug(s"Sending ${outPacket.kind} to ${node.address}")
          ToxPacket.toBytes(outPacket) match {
            case -\/(error) =>
              Process.fail(error.exception)
            case \/-(packetData) =>
              udp.send(node.address, packetData.toByteVector)
          }

        case action =>
          logger.debug("UDP loop ignored " + action)
          Process.empty
      }

      for {
        _ <- udp.merge(sender, receiver)
      } yield {
        logger.debug("Processed UDP event or performed UDP action")
      }
    }
  }

}
