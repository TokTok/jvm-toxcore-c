package im.tox.core.dht.handlers

import im.tox.core.dht.packets.DhtUnencryptedPacket
import im.tox.core.dht.{Dht, NodeInfo}
import im.tox.core.error.CoreError
import im.tox.core.io.IO
import im.tox.core.typesafe.Security

import scalaz.\/

final case class DhtUnencryptedHandler[T, S <: Security](handler: DhtUnencryptedPayloadHandler[T, S])
    extends DhtEncryptedPayloadHandler(DhtUnencryptedPacket.Make(handler.module)) {

  override def apply(dht: Dht, sender: NodeInfo, packet: DhtUnencryptedPacket[T]): CoreError \/ IO[Dht] = {
    handler(dht, sender, packet.payload, packet.pingId)
  }

}
