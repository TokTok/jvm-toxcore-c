package im.tox.core.dht.handlers

import im.tox.core.dht.packets.dht.{NodesRequestPacket, NodesResponsePacket}
import im.tox.core.dht.{Dht, NodeInfo}
import im.tox.core.error.CoreError
import im.tox.core.io.IO

import scalaz.\/

/**
 * Get node requests are responded to by send node responses. Send node
 * responses should contain the 4 closest good (not timed out) nodes that the
 * node receiving the get node has in their list of known nodes.
 */
object NodesRequestHandler extends DhtPayloadHandler(NodesRequestPacket) {

  /**
   * When receiving a get node packet, toxcore will find the 4 nodes, in its nodes
   * lists, closest to the public key in the packet and send them in the send node
   * response.
   */
  override def apply(dht: Dht, sender: NodeInfo, packet: NodesRequestPacket): CoreError \/ IO[Dht] = {
    val nearNodes = dht.getNearNodes(NodesResponsePacket.MaxNodes, packet.requestedNode).toList

    for {
      response <- makeResponse(
        dht.keyPair,
        sender.publicKey,
        NodesResponsePacket,
        NodesResponsePacket(nearNodes, packet.pingId)
      )
    } yield {
      for {
        () <- IO.sendTo(sender, response)
      } yield {
        dht
      }
    }
  }

}
