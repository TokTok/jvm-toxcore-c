package im.tox.core.dht.handlers

import com.typesafe.scalalogging.Logger
import im.tox.core.dht.packets.dht.{PingPacket, PingRequestPacket, PingResponsePacket}
import im.tox.core.dht.{Dht, NodeInfo, PacketBuilder}
import im.tox.core.error.CoreError
import im.tox.core.io.IO
import im.tox.core.network.PacketKind
import org.slf4j.LoggerFactory

import scala.concurrent.duration.FiniteDuration
import scalaz.{\/, \/-}

case object PingResponseHandler extends DhtUnencryptedPayloadHandler(PingResponsePacket) {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  override def apply(
    dht: Dht,
    sender: NodeInfo,
    packet: PingPacket[PacketKind.PingResponse.type],
    pingId: Long
  ): CoreError \/ IO[Dht] = \/- {
    for {
      _ <- installPingTimer(dht.options.pingInterval, sender)
      _ <- installPingTimeoutTimer(dht.options.pingTimeout, sender)
    } yield {
      /**
       * Nodes are only added to the lists after a valid ping response or send node
       * packet is received from them.
       */
      dht.addNode(sender)
    }
  }

  private val pingTimer = IO.TimerIdFactory("Ping")
  private val pingTimeoutTimer = IO.TimerIdFactory("PingTimeout")

  /**
   * Install ping timer: after [[Dht.Options.pingInterval]] seconds, ping again.
   */
  private def installPingTimer(pingInterval: FiniteDuration, sender: NodeInfo): IO[Unit] = {
    IO.timedAction(pingTimer(sender.publicKey.toHexString), pingInterval) { (duration, dht) =>
      for {
        pingRequest <- PacketBuilder.makeResponse(
          dht.keyPair,
          sender.publicKey,
          PingRequestPacket,
          PingRequestPacket,
          0
        )
      } yield {
        logger.debug(s"Sending ping to $sender after ping interval: ${duration.toSeconds} seconds")
        for {
          _ <- IO.sendTo(sender, pingRequest)
        } yield {
          dht
        }
      }
    }
  }

  /**
   * Install ping timeout timer: after [[Dht.Options.pingTimeout]] seconds, remove the node from
   * the DHT node lists and cancel the ping timer.
   */
  private def installPingTimeoutTimer(pingTimeout: FiniteDuration, sender: NodeInfo): IO[Unit] = {
    IO.timedAction(pingTimeoutTimer(sender.publicKey.toHexString), pingTimeout, Some(1)) { (duration, dht) =>
      \/- {
        logger.debug(s"Removing node $sender after ping timeout: ${duration.toSeconds} seconds")
        for {
          _ <- IO.cancelTimer(pingTimer(sender.publicKey.toHexString))
        } yield {
          dht.removeNode(sender)
        }
      }
    }
  }

}
