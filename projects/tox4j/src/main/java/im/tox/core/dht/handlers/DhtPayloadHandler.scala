package im.tox.core.dht.handlers

import im.tox.core.ModuleCompanion
import im.tox.core.crypto.{KeyPair, Nonce, PublicKey}
import im.tox.core.dht.packets.DhtEncryptedPacket
import im.tox.core.dht.{Dht, NodeInfo}
import im.tox.core.error.CoreError
import im.tox.core.io.IO
import im.tox.core.network.packets.ToxPacket
import im.tox.core.network.{PacketKind, PacketModuleCompanion}
import im.tox.core.typesafe.Security

import scalaz.\/

/**
 * Base class for handlers that receive a [[DhtEncryptedPacket]]'s payload.
 */
abstract class DhtPayloadHandler[T, S <: Security](val module: ModuleCompanion[T, S]) {

  def apply(dht: Dht, sender: NodeInfo, packet: T): CoreError \/ IO[Dht]

  private def makeResponse[Response, Kind <: PacketKind](
    packetModule: PacketModuleCompanion[Response, Kind, Security.Sensitive],
    dhtPacket: DhtEncryptedPacket[Response]
  ): CoreError \/ ToxPacket[Kind] = {
    val encryptedPacketModule = DhtEncryptedPacket.Make(packetModule)

    for {
      bytes <- encryptedPacketModule.toBytes(dhtPacket)
    } yield {
      ToxPacket(
        packetModule.packetKind,
        bytes
      )
    }
  }

  def makeResponse[Response, Kind <: PacketKind](
    senderKeyPair: KeyPair,
    receiverPublicKey: PublicKey,
    packetModule: PacketModuleCompanion[Response, Kind, Security.Sensitive],
    packet: Response
  ): CoreError \/ ToxPacket[Kind] = {
    val encryptedPacketModule = DhtEncryptedPacket.Make(packetModule)

    for {
      dhtPacket <- encryptedPacketModule.encrypt(
        receiverPublicKey,
        senderKeyPair,
        Nonce.random(),
        packet
      )
      response <- makeResponse(packetModule, dhtPacket)
    } yield {
      response
    }
  }

  override def toString: String = {
    getClass.getSimpleName
  }

}
