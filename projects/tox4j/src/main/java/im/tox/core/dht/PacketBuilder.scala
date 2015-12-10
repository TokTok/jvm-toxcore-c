package im.tox.core.dht

import im.tox.core.crypto.{KeyPair, Nonce, PublicKey}
import im.tox.core.dht.packets.{DhtEncryptedPacket, DhtUnencryptedPacket}
import im.tox.core.error.CoreError
import im.tox.core.network.packets.ToxPacket
import im.tox.core.network.{PacketKind, PacketModuleCompanion}
import im.tox.core.typesafe.Security

import scalaz.\/

object PacketBuilder {

  def makeResponse[Response, Kind <: PacketKind](
    senderKeyPair: KeyPair,
    receiverPublicKey: PublicKey,
    packetModule: PacketModuleCompanion[Response, Kind, Security.Sensitive],
    packet: Response,
    pingId: Long
  ): CoreError \/ ToxPacket[Kind] = {
    val encryptedPacketModule = DhtEncryptedPacket.Make(DhtUnencryptedPacket.Make(packetModule))

    for {
      dhtPacket <- encryptedPacketModule.encrypt(
        receiverPublicKey,
        senderKeyPair,
        Nonce.random(),
        DhtUnencryptedPacket(packet, pingId)
      )
      bytes <- encryptedPacketModule.toBytes(dhtPacket)
    } yield {
      ToxPacket(
        packetModule.packetKind,
        bytes
      )
    }
  }

}
