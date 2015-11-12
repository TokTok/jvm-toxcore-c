package im.tox.core.dht.packets

import im.tox.core.ModuleCompanion
import im.tox.core.crypto.PublicKey
import im.tox.core.network.{PacketKind, PacketModuleCompanion}
import im.tox.core.typesafe.Security

/**
 * DHT Request packets:
 * [char with a value of 32]
 * [The receiver's DHT Public key (32 bytes))]
 * [The sender's DHT Public key (32 bytes)]
 * [Random nonce (24 bytes)]
 * [Encrypted message]
 *
 * DHT Request packets are packets that can be sent across one DHT node to one
 * that they know. They are used to send encrypted data to friends that we are
 * not necessarily connected to directly in the DHT.
 *
 * DHT request packets are used for DHTPK packets (see onion) and NAT ping
 * packets.
 */
final case class DhtRequestPacket[Payload](
  receiverPublicKey: PublicKey,
  payload: DhtEncryptedPacket[Payload]
)

object DhtRequestPacket {

  final case class Make[Payload, S <: Security](module: ModuleCompanion[Payload, S])
      extends PacketModuleCompanion[DhtRequestPacket[Payload], PacketKind.DhtRequest.type, Security.NonSensitive](PacketKind.DhtRequest) {

    override val codec =
      (PublicKey.codec ~ DhtEncryptedPacket.Make(module).codec).xmap[DhtRequestPacket[Payload]](
        { case (receiverPublicKey, payload) => DhtRequestPacket(receiverPublicKey, payload) },
        { case DhtRequestPacket(receiverPublicKey, payload) => (receiverPublicKey, payload) }
      )

  }

}
