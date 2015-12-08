package im.tox.core.dht.handlers

import im.tox.core.dht.packets.dht.{PingPacket, PingRequestPacket, PingResponsePacket}
import im.tox.core.dht.{Dht, NodeInfo}
import im.tox.core.error.CoreError
import im.tox.core.io.IO
import im.tox.core.network.PacketKind

import scalaz.\/

case object PingResponseHandler extends DhtUnencryptedPayloadHandler(PingResponsePacket) {

  private val pingTimer = IO.TimerIdFactory("Ping")
  private val pingTimeoutTimer = IO.TimerIdFactory("PingTimeout")

  override def apply(
    dht: Dht,
    sender: NodeInfo,
    packet: PingPacket[PacketKind.PingResponse.type],
    pingId: Long
  ): CoreError \/ IO[Dht] = {
    for {
      pingRequest <- makeResponse(
        dht.keyPair,
        sender.publicKey,
        PingRequestPacket,
        PingRequestPacket,
        0
      )
    } yield {
      for {
        /**
         * Install ping timer: after [[Dht.PingInterval]] seconds, ping again.
         */
        _ <- IO.timedAction(pingTimer(sender.publicKey.readable), dht.settings(Dht.PingInterval)) { (_, dht) =>
          for {
            _ <- IO.sendTo(sender, pingRequest)
          } yield {
            dht
          }
        }

        /**
         * Install ping timeout timer: after [[Dht.PingTimeout]] seconds, remove the node from
         * the DHT node lists and cancel the ping timer.
         */
        _ <- IO.timedAction(pingTimeoutTimer(sender.publicKey.readable), dht.settings(Dht.PingTimeout), Some(1)) { (_, dht) =>
          for {
            _ <- IO.cancelTimer(pingTimer(sender.publicKey.readable))
          } yield {
            dht.removeNode(sender)
          }
        }
      } yield {
        /**
         * Nodes are only added to the lists after a valid ping response or send node
         * packet is received from them.
         */
        dht.addNode(sender)
      }
    }
  }

}
