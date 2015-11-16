package im.tox.core.network

import java.net.InetSocketAddress

import com.typesafe.scalalogging.Logger
import im.tox.core.crypto.PlainText.Conversions._
import im.tox.core.crypto.PublicKey
import im.tox.core.dht.packets.dht.{NodesRequestPacket, PingRequestPacket}
import im.tox.core.dht.{Dht, NodeInfo, Protocol}
import im.tox.core.io.{EventActor, TimeActor, UdpActor, IO}
import im.tox.core.network.packets.ToxPacket
import im.tox.tox4j.core.ToxCoreConstants
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.language.postfixOps
import scalaz.concurrent.Task
import scalaz.stream._
import scalaz.{-\/, \/-}

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
@SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Any")) // scalastyle:off
object NetworkCore {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  /**
   * Create a new DHT node.
   *
   * The initial packet should be a [[PingRequestPacket]] or [[NodesRequestPacket]].
   *
   * @param dht The initial DHT state.
   * @param bootstrapAddress Network address of the first node we send the initial packet to.
   * @param bootstrapPublicKey DHT public key of the node we're sending the packet to.
   * @param bootstrapRequest The initial packet data.
   */
  def client(
    dht: Dht,
    bootstrapAddress: InetSocketAddress,
    bootstrapPublicKey: PublicKey,
    bootstrapRequest: ToxPacket[PacketKind]
  ): Process[Task, Unit] = {
    val eventQueue = async.boundedQueue[IO.Event](10)
    val actionTopic = async.topic[IO.Action]()

    val actionLoop = EventActor.make(dht)(eventQueue.dequeue, actionTopic.publish)

    val udpLoop = UdpActor.make(actionTopic.subscribe, eventQueue.enqueue)
    val timeLoop = TimeActor.make(actionTopic.subscribe, eventQueue.enqueue)

    val bootstrapAction = Process(
      IO.Action.SendTo(
        NodeInfo(Protocol.Udp, bootstrapAddress, bootstrapPublicKey),
        bootstrapRequest
      ),
      IO.Action.SendTo(
        NodeInfo(Protocol.Udp, bootstrapAddress, bootstrapPublicKey),
        bootstrapRequest
      ),
      IO.Action.StartTimer(1 second, 1) { duration => Some(IO.TimeEvent(duration)) },
      IO.Action.StartTimer(1 second, 1) { duration => Some(IO.TimeEvent(duration)) },
      IO.Action.StartTimer(1 second, 1) { duration => Some(IO.TimeEvent(duration)) },
      IO.Action.StartTimer(1 second, 1) { duration => Some(IO.TimeEvent(duration)) },
      IO.Action.StartTimer(1 second, 1) { duration => Some(IO.ShutdownEvent) }
    ).toSource.to(actionTopic.publish)

    val startLoop = bootstrapAction.merge(udpLoop).merge(timeLoop).merge(actionLoop)

    val shutdownLoop =
      actionTopic.subscribe.flatMap {
        case IO.Action.Shutdown =>
          println("SHUTDOWN")
          startLoop.kill ++ Process.halt
        case _ =>
          Process.empty[Task, Unit]
      }

    startLoop.merge(shutdownLoop.drain)
  }

}
