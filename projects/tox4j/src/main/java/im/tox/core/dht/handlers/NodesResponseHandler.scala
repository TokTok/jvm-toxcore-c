package im.tox.core.dht.handlers

import im.tox.core.dht.packets.dht.{NodesResponsePacket, PingPacket, PingRequestPacket}
import im.tox.core.dht.{Dht, NodeInfo}
import im.tox.core.error.CoreError
import im.tox.core.io.IO
import im.tox.core.network.packets.ToxPacket

import scalaz.{\/, \/-}

/**
 * When receiving a send node packet, toxcore will check if each of the received
 * nodes could be added to any one of the lists. If the node can, toxcore will
 * send a ping packet to it, if it cannot it will be ignored.
 */
object NodesResponseHandler extends DhtPayloadHandler(NodesResponsePacket) {

  override def apply(dht: Dht, sender: NodeInfo, packet: NodesResponsePacket): CoreError \/ IO[Dht] = {
    val nodes = packet.nodes.filter(dht.canAddNode)

    val packets = nodes.foldLeft(\/-(Nil): CoreError \/ List[(NodeInfo, ToxPacket[PingRequestPacket.PacketKind])]) { (packets, node) =>
      for {
        packets <- packets
        response <- makeResponse(dht.keyPair, node.publicKey, PingRequestPacket, PingPacket(0))
      } yield {
        (node, response) +: packets
      }
    }

    for {
      packets <- packets
    } yield {
      packets.foldLeft(IO(dht)) {
        case (dht: IO[Dht], (node, packet)) =>
          IO.sendTo(node, packet).flatMap(_ => dht)
      }
    }
  }

}
