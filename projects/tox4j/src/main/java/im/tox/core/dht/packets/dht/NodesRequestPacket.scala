package im.tox.core.dht.packets.dht

import java.io.{DataInputStream, DataOutput}

import im.tox.core.crypto.PublicKey
import im.tox.core.error.DecoderError
import im.tox.core.network.{PacketKind, PacketModuleCompanion}

import scalaz.\/

/**
 * Get nodes (Request):
 * Packet contents:
 *
 * [byte with value: 02]
 * [DHT public key of sender, length=32 bytes]
 * [random 24 byte nonce]
 * [Encrypted with the nonce, DHT private key of the sender and public DHT key of the receiver :
 *   [requested_node public key (DHT public key of node of which we want to find), length=32 bytes]
 *   [ping_id (must be sent back unmodified by in the response), length=8 bytes]
 * ]
 *
 * Valid replies: a send_nodes packet
 *
 * The first byte of a get node request is a 2. This is then followed by the
 * DHT public key of the sender and a nonce. Inside the encrypted part of the
 * request is the DHT key that the sender is searching for or wants to find the
 * nodes in the DHT closest to it. This is followed by an 8 byte ping id which
 * is there for the same reason as the one for the ping request.
 */
final case class NodesRequestPacket(
  requestedNode: PublicKey,
  pingId: Long
)

object NodesRequestPacket
    extends PacketModuleCompanion[NodesRequestPacket, PacketKind.NodesRequest.type](PacketKind.NodesRequest) {

  override def write(self: NodesRequestPacket, packetData: DataOutput): Unit = {
    PublicKey.write(self.requestedNode, packetData)
    packetData.writeLong(self.pingId)
  }

  override def read(packetData: DataInputStream): DecoderError \/ NodesRequestPacket = {
    for {
      requestedNode <- PublicKey.read(packetData)
    } yield {
      val pingId = packetData.readLong()
      NodesRequestPacket(
        requestedNode,
        pingId
      )
    }
  }

}
