package im.tox.core.dht.handlers

import im.tox.core.ModuleCompanion
import im.tox.core.crypto.{PublicKey, KeyPair, Nonce, PlainText}
import im.tox.core.dht.packets.DhtEncryptedPacket
import im.tox.core.dht.{Dht, NodeInfo}
import im.tox.core.error.DecoderError
import im.tox.core.io.IO
import im.tox.core.network.packets.ToxPacket
import im.tox.core.network.{PacketKind, PacketModuleCompanion}

import scalaz.\/

/**
 * Base class for handlers that receive a [[DhtEncryptedPacket]]'s payload.
 */
abstract class DhtPayloadHandler[T](val module: ModuleCompanion[T]) {

  def apply(dht: Dht, sender: NodeInfo, packet: T): DecoderError \/ IO[Dht]

  private def makeResponse[Response, Kind <: PacketKind](
    packetModule: PacketModuleCompanion[Response, Kind],
    dhtPacket: DhtEncryptedPacket[Response]
  ): ToxPacket[packetModule.PacketKind] = {
    val encryptedPacketModule = DhtEncryptedPacket.Make(packetModule)

    ToxPacket(
      packetModule.packetKind,
      PlainText(encryptedPacketModule.toBytes(dhtPacket))
    )
  }

  def makeResponse[Response, Kind <: PacketKind](
    senderKeyPair: KeyPair,
    receiverPublicKey: PublicKey,
    packetModule: PacketModuleCompanion[Response, Kind],
    packet: Response
  ): ToxPacket[packetModule.PacketKind] = {
    val encryptedPacketModule = DhtEncryptedPacket.Make(packetModule)

    val dhtPacket = encryptedPacketModule.encrypt(
      receiverPublicKey,
      senderKeyPair,
      Nonce.random(),
      packet
    )

    makeResponse(packetModule, dhtPacket)
  }

  override def toString: String = {
    getClass.getSimpleName
  }

}
