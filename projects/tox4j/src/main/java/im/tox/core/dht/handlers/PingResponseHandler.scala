package im.tox.core.dht.handlers

import im.tox.core.dht.packets.dht.{PingRequestPacket, NodesRequestPacket, PingPacket, PingResponsePacket}
import im.tox.core.dht.{Dht, NodeInfo}
import im.tox.core.error.CoreError
import im.tox.core.io.IO
import im.tox.core.network.PacketKind
import im.tox.core.network.packets.ToxPacket

import scalaz.\/

object PingResponseHandler extends DhtPayloadHandler(PingResponsePacket) {

  override def apply(dht: Dht, sender: NodeInfo, packet: PingPacket): CoreError \/ IO[Dht] = {
    for {
      pingRequest <- makeResponse(
        dht.keyPair,
        sender.publicKey,
        PingRequestPacket,
        PingPacket(0)
      )
    } yield {
      for {
        /**
         * Install ping timer: after [[Dht.PingInterval]] seconds, ping again.
         */
        _ <- IO.timedAction(Dht.PingInterval, Some(1)) { (_, dht) =>
          for {
            _ <- IO.sendTo(sender, pingRequest)
          } yield {
            dht
          }
        }

        /**
         * Install ping timeout timer: after [[Dht.PingTimeout]] seconds, remove the node from
         * the DHT node lists.
         */
        _ <- IO.timedAction(Dht.PingTimeout, Some(1)) { (_, dht) =>
          IO(dht.removeNode(sender))
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
