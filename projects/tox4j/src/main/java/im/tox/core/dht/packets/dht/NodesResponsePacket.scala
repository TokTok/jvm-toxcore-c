package im.tox.core.dht.packets.dht

import java.io.{DataInputStream, DataOutput}

import im.tox.core.dht.NodeInfo
import im.tox.core.error.DecoderError
import im.tox.core.network.{PacketKind, PacketModuleCompanion}

import scala.annotation.tailrec
import scala.collection.GenTraversable
import scalaz.{-\/, \/, \/-}

/**
 * Send_nodes (response):
 *
 * [byte with value: 04]
 * [DHT public key of sender, length=32 bytes]
 * [random 24 byte nonce]
 * [Encrypted with the nonce and private key of the sender:
 *   [uint8_t number of nodes in this packet]
 *   [Nodes in packed node format, length = (39 bytes for ipv4, 41 bytes for ipv6) * (number of nodes (maximum of 4 nodes)) bytes]
 *   [ping_id, length=8 bytes]
 * ]
 *
 * The first byte of a send node response is a 4. This is then followed by the
 * DHT public key of the sender and a nonce. Inside the encrypted part of the
 * response there is a 1 byte number which contains the number of nodes stored
 * in the packet, up to 4 nodes in packed node format and the 8 byte ping id
 * that was sent in the request.
 */
final case class NodesResponsePacket(
  nodes: GenTraversable[NodeInfo],
  pingId: Long
)

object NodesResponsePacket
    extends PacketModuleCompanion[NodesResponsePacket, PacketKind.NodesResponse.type](PacketKind.NodesResponse) {

  val MaxNodes = 4

  override def write(self: NodesResponsePacket, packetData: DataOutput): Unit = {
    /**
     * [uint8_t number of nodes in this packet]
     *
     * (maximum of 4 nodes)
     */
    assert(self.nodes.size <= MaxNodes)
    packetData.write(self.nodes.size)

    /**
     * [Nodes in packed node format, length = (39 bytes for ipv4, 41 bytes for ipv6) * (number of nodes (maximum of 4 nodes)) bytes]
     */
    for (node <- self.nodes) {
      NodeInfo.write(node, packetData)
    }

    /**
     * [ping_id, length=8 bytes]
     */
    packetData.writeLong(self.pingId)
  }

  @tailrec
  private def readNodes(packetData: DataInputStream, count: Int, nodes: Seq[NodeInfo]): DecoderError \/ Seq[NodeInfo] = {
    count match {
      case 0 =>
        \/-(nodes.reverse)

      case _ =>
        NodeInfo.read(packetData) match {
          case error @ -\/(_) => error
          case \/-(node)      => readNodes(packetData, count - 1, node +: nodes)
        }
    }
  }

  override def read(packetData: DataInputStream): DecoderError \/ NodesResponsePacket = {
    for {
      nodes <- {
        val nodeCount = packetData.read()
        if (nodeCount > MaxNodes) {
          -\/(DecoderError.InvalidFormat(s"Too many nodes: $nodeCount > $MaxNodes"))
        } else {
          readNodes(packetData, nodeCount, Nil)
        }
      }
    } yield {
      val pingId = packetData.readLong()
      NodesResponsePacket(
        nodes,
        pingId
      )
    }
  }

}
