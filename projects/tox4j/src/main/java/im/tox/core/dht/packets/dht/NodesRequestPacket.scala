package im.tox.core.dht.packets.dht

import im.tox.core.crypto.PublicKey
import im.tox.core.network.{PacketKind, PacketModuleCompanion}
import im.tox.core.typesafe.Security
import scodec.codecs.int64

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
    extends PacketModuleCompanion[NodesRequestPacket, PacketKind.NodesRequest.type, Security.Sensitive](PacketKind.NodesRequest) {

  override val codec =
    (PublicKey.codec ~ int64).xmap[NodesRequestPacket](
      { case (publicKey, pingId) => NodesRequestPacket(publicKey, pingId) },
      { case NodesRequestPacket(publicKey, pingId) => (publicKey, pingId) }
    )

}
