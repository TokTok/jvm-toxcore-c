package im.tox.core.dht.handlers

import java.net.InetSocketAddress

import im.tox.core.dht.{Protocol, NodeInfo, Dht}
import im.tox.core.dht.packets.DhtEncryptedPacket
import im.tox.core.error.DecoderError
import im.tox.core.io.IO
import im.tox.core.network.handlers.ToxPacketHandler

import scalaz.\/

final case class DhtEncryptedHandler[T](handler: DhtPayloadHandler[T])
    extends ToxPacketHandler(DhtEncryptedPacket.Make(handler.module)) {

  override val module = DhtEncryptedPacket.Make(handler.module)

  override def apply(dht: Dht, origin: InetSocketAddress, dhtPacket: DhtEncryptedPacket[T]): DecoderError \/ IO[Dht] = {
    for {
      decryptedPacket <- module.decrypt(dhtPacket, dht.keyPair.secretKey)
      dht <- handler(dht, NodeInfo(Protocol.Tcp, origin, dhtPacket.senderPublicKey), decryptedPacket)
    } yield {
      dht
    }
  }

}
