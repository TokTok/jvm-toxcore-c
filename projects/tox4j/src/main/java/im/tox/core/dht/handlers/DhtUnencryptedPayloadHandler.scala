package im.tox.core.dht.handlers

import im.tox.core.ModuleCompanion
import im.tox.core.crypto.{KeyPair, Nonce, PublicKey}
import im.tox.core.dht.packets.{DhtEncryptedPacket, DhtUnencryptedPacket}
import im.tox.core.dht.{Dht, NodeInfo}
import im.tox.core.error.CoreError
import im.tox.core.io.IO
import im.tox.core.network.packets.ToxPacket
import im.tox.core.network.{PacketKind, PacketModuleCompanion}
import im.tox.core.typesafe.Security

import scalaz.\/

/**
 * Base class for handlers that receive a [[DhtUnencryptedPacket]]'s payload.
 */
abstract class DhtUnencryptedPayloadHandler[T, S <: Security](val module: ModuleCompanion[T, S]) {

  def apply(dht: Dht, sender: NodeInfo, packet: T, pingId: Long): CoreError \/ IO[Dht]

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
