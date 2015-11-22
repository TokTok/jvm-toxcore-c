package im.tox.core.dht.handlers

import im.tox.core.Functional.foldDisjunctionList
import im.tox.core.dht.packets.dht.{NodesResponsePacket, PingRequestPacket}
import im.tox.core.dht.{Dht, NodeInfo}
import im.tox.core.error.CoreError
import im.tox.core.io.IO

import scalaz.\/

/**
 * When receiving a send node packet, toxcore will check if each of the received
 * nodes could be added to any one of the lists. If the node can, toxcore will
 * send a ping packet to it, if it cannot it will be ignored.
 */
object NodesResponseHandler extends DhtUnencryptedPayloadHandler(NodesResponsePacket) {

  override def apply(dht: Dht, sender: NodeInfo, packet: NodesResponsePacket, pingId: Long): CoreError \/ IO[Dht] = {
    // The nodes we could add to the DHT node lists if they reply to our ping.
    val potentialNodes = packet.nodes.filter(dht.canAddNode)

    // Create ping packets for each of them.
    val pingPackets =
      potentialNodes.map { node =>
        makeResponse(
          dht.keyPair,
          node.publicKey,
          PingRequestPacket,
          PingRequestPacket,
          0
        )
          // Pair up the response with the node it should be sent to.
          .map((node, _))
      }

    for {
      // Get the list of packets or fail if any of them failed to construct.
      pingPackets <- foldDisjunctionList(pingPackets)
    } yield {
      // Send all the ping packets.
      pingPackets.foldLeft(IO(dht)) {
        case (dht, (node, pingPacket)) =>
          IO.sendTo(node, pingPacket).flatMap { case () => dht }
      }
    }
  }

}
