package im.tox.core.dht.handlers

import im.tox.core.ModuleCompanion
import im.tox.core.dht.packets.DhtEncryptedPacket
import im.tox.core.dht.{Dht, NodeInfo}
import im.tox.core.error.CoreError
import im.tox.core.io.IO
import im.tox.core.typesafe.Security

import scalaz.\/

/**
 * Base class for handlers that receive a [[DhtEncryptedPacket]]'s payload.
 */
abstract class DhtEncryptedPayloadHandler[T, S <: Security](val module: ModuleCompanion[T, S]) {

  def apply(dht: Dht, sender: NodeInfo, packet: T): CoreError \/ IO[Dht]

}
