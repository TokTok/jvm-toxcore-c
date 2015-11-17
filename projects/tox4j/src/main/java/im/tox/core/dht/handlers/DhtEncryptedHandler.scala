package im.tox.core.dht.handlers

import java.net.InetSocketAddress

import im.tox.core.crypto.KeyPair
import im.tox.core.dht.packets.DhtEncryptedPacket
import im.tox.core.dht.{Dht, NodeInfo, Protocol}
import im.tox.core.error.CoreError
import im.tox.core.io.IO
import im.tox.core.network.handlers.ToxPacketHandler
import im.tox.core.typesafe.Security

import scalaz.{\/-, -\/, \/}

final case class DhtEncryptedHandler[T, S <: Security](handler: DhtPayloadHandler[T, S])
    extends ToxPacketHandler(DhtEncryptedPacket.Make(handler.module)) {

  override val module = DhtEncryptedPacket.Make(handler.module)

  override def apply(dht: Dht, origin: InetSocketAddress, dhtPacket: DhtEncryptedPacket[T]): CoreError \/ IO[Dht] = {
    for {
      payload <- module.decrypt(dhtPacket, dht.keyPair.secretKey)
      dht <- handler(dht, NodeInfo(Protocol.Tcp, origin, dhtPacket.senderPublicKey), payload)
    } yield {
      dht
    }
  }

  override def toString(keyPair: KeyPair, dhtPacket: DhtEncryptedPacket[T]): String = {
    module.decrypt(dhtPacket, keyPair.secretKey) match {
      case -\/(error) =>
        error.toString
      case \/-(payload) =>
        s"${dhtPacket.getClass.getSimpleName}(${dhtPacket.senderPublicKey}, ${dhtPacket.nonce}, $payload)"
    }
  }

}
