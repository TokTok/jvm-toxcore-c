package im.tox.core.network

import java.net.InetSocketAddress

import com.typesafe.scalalogging.Logger
import im.tox.core.crypto.PlainText.Conversions._
import im.tox.core.crypto.{Nonce, PlainText, PublicKey}
import im.tox.core.dht.Dht
import im.tox.core.dht.packets.DhtEncryptedPacket
import im.tox.core.dht.packets.dht._
import im.tox.core.error.CoreError
import im.tox.core.io.IO
import im.tox.core.network.packets.ToxPacket
import im.tox.tox4j.core.ToxCoreConstants
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.language.postfixOps
import scalaz.stream._
import scalaz.stream.udp.{Connection, Packet}
import scalaz.{-\/, \/, \/-}

/**
 * The network module is the lowest file in toxcore that everything else depends
 * on. This module is basically a UDP socket wrapper, serves as the sorting
 * ground for packets received by the socket, initializes and uninitializes the
 * socket. It also contains many socket, networking related and some other
 * functions like a monotonic time function used by other toxcore modules.
 *
 * networking_registerhandler()
 * is used by higher level modules in order to tell the network object which
 * packets to send to which module via a callback.
 *
 * Since the network module interacts directly with the underlying operating
 * system with its socket functions it has code to make it work on windows,
 * linux, etc... unlike most modules that sit at a higher level.
 *
 * The network module currently uses the polling method to read from the UDP
 * socket. The networking_poll() function is called to read all the packets from
 * the socket and pass them to the callbacks set using the
 * networking_registerhandler() function. The reason it uses polling is simply
 * because it was easier to write it that way, another method would be better
 * here.
 *
 * The goal of this module is to provide an easy interface to a UDP socket and
 * other networking related functions.
 */
@SuppressWarnings(Array(
  "org.brianmckenna.wartremover.warts.Any",
  "org.brianmckenna.wartremover.warts.AsInstanceOf",
  "org.brianmckenna.wartremover.warts.IsInstanceOf"
)) // scalastyle:off
object NetworkCore {

  implicit val scheduler = scalaz.stream.DefaultScheduler

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  /**
   * Things of note in this module are the maximum UDP packet size define
   * (MAX_UDP_PACKET_SIZE) which sets the maximum UDP packet size toxcore can send
   * and receive.
   */
  val MaxUdpPacketSize = 2048

  trait Event
  final case class TimeEvent(duration: Duration) extends Event
  final case class NetworkEvent(packet: Packet) extends Event

  def performAction(action: IO.Action): Process[Connection, Unit] = {
    action match {
      case IO.Action.SendTo(node, outPacket) =>
        logger.debug(s"Sending ${outPacket.kind} to ${node.address}")
        ToxPacket.toBytes(outPacket) match {
          case -\/(error) =>
            Process.fail(error.exception)
          case \/-(packetData) =>
            udp.send(to = node.address, packetData.toByteVector)
        }
    }
  }

  def processNetworkEvent(dht: Dht, packet: Packet): Process[Connection, Dht] = {
    logger.debug("Network event: " + packet)

    val result =
      for {
        dht <- ToxHandler(dht, packet.origin, PlainText(packet.bytes))
      } yield {
        for {
          action <- Process.emitAll(dht.actions): Process[Connection, IO.Action]
          () <- performAction(action)
        } yield {
          dht.value
        }
      }

    result match {
      case -\/(error) =>
        logger.error(s"Fail: $error on $packet", error.exception)
        Process.emit(dht)
      case \/-(dht) =>
        dht
    }
  }

  def processTimingEvent(dht: Dht, duration: Duration): Process[Connection, Dht] = {
    logger.debug("Time event: " + duration.toSeconds)
    Process.emit(dht)
  }

  def processEvent(dht: Dht, event: Event): Process[Connection, Dht] = {
    event match {
      case NetworkEvent(packet) =>
        processNetworkEvent(dht, packet)
      case TimeEvent(duration) =>
        processTimingEvent(dht, duration)
    }
  }

  val EncryptedNodesRequestPacket = DhtEncryptedPacket.Make(NodesRequestPacket)

  val nodes = Seq(
    ("192.210.149.121", "F404ABAA1C99A9D37D61AB54898F56793E1DEF8BD46B1038B9D822E8460FAB67"),
    ("178.62.250.138", "788236D34978D1D5BD822F0A5BEBD2C53C64CC31CD3149350EE27D4D9A2F9B6B")
  )

  def start(): \/[CoreError, Option[Dht]] = {
    val node = nodes(0)
    val address = new InetSocketAddress(node._1, ToxCoreConstants.DefaultStartPort)
    for {
      receiverPublicKey <- PublicKey.fromString(node._2)
      result <- {
        val dht = Dht()

        val nodesRequestPacket = NodesRequestPacket(dht.keyPair.publicKey, 1)

        for {
          request <- EncryptedNodesRequestPacket.encrypt(
            receiverPublicKey,
            dht.keyPair,
            Nonce.random(),
            nodesRequestPacket
          )
          request <- EncryptedNodesRequestPacket.toBytes(request)
          request <- ToxPacket.toBytes(ToxPacket(
            NodesRequestPacket.packetKind,
            request
          ))
        } yield {
          val timer = udp.lift(time.awakeEvery(1 seconds).take(5).map(TimeEvent))
          val receiver = udp.receives(MaxUdpPacketSize, Some(5 seconds)).take(20).map(NetworkEvent)

          val client = udp.listen(ToxCoreConstants.DefaultStartPort) {
            for {
              () <- udp.send(to = address, request.toByteVector)
              event <- udp.merge(timer, receiver)
              dht <- processEvent(dht, event)
            } yield {
              logger.info("Finished: " + dht)
              dht
            }
          }

          val result = client.runLast.run
          logger.info("Final state: " + result)
          result
        }
      }
    } yield {
      result
    }
  }

}
