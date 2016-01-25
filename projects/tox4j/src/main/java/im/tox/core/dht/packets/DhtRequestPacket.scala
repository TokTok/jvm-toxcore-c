package im.tox.core.dht.packets

import java.io.{DataInputStream, DataOutput}

import im.tox.core.ModuleCompanion
import im.tox.core.crypto.PublicKey
import im.tox.core.error.DecoderError
import im.tox.core.network.{PacketKind, PacketModuleCompanion}

import scalaz.\/

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

  final case class Make[Payload](module: ModuleCompanion[Payload])
      extends PacketModuleCompanion[DhtRequestPacket[Payload], PacketKind.DhtRequest.type](PacketKind.DhtRequest) {

    override def write(self: DhtRequestPacket[Payload], packetData: DataOutput): Unit = {
      PublicKey.write(self.receiverPublicKey, packetData)
      DhtEncryptedPacket.Make(module).write(self.payload, packetData)
    }

    override def read(packetData: DataInputStream): DecoderError \/ DhtRequestPacket[Payload] = {
      for {
        receiverPublicKey <- PublicKey.read(packetData)
        payload <- DhtEncryptedPacket.Make(module).read(packetData)
      } yield {
        DhtRequestPacket(
          receiverPublicKey,
          payload
        )
      }
    }

  }

}
