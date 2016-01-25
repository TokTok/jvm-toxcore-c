package im.tox.core.dht.handlers

import im.tox.core.dht.packets.dht.{NodesResponsePacket, PingPacket, PingRequestPacket}
import im.tox.core.dht.{Dht, NodeInfo}
import im.tox.core.error.DecoderError
import im.tox.core.io.IO

import scalaz.{\/, \/-}

/**
 * When receiving a send node packet, toxcore will check if each of the received
 * nodes could be added to any one of the lists. If the node can, toxcore will
 * send a ping packet to it, if it cannot it will be ignored.
 */
object NodesResponseHandler extends DhtPayloadHandler(NodesResponsePacket) {

  override def apply(dht: Dht, sender: NodeInfo, packet: NodesResponsePacket): DecoderError \/ IO[Dht] = {
    val ioDht = packet.nodes.filter(dht.canAddNode).foldLeft(IO(dht)) {
      case (ioDht, node) =>
        for {
          dht <- ioDht
          () <- IO.sendTo(node, makeResponse(dht.keyPair, node.publicKey, PingRequestPacket, PingPacket(0)))
        } yield {
          dht
        }
    }

    \/-(ioDht)
  }

}
