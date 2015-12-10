package im.tox.core.dht.handlers

import im.tox.core.ModuleCompanion
import im.tox.core.dht.packets.DhtUnencryptedPacket
import im.tox.core.dht.{Dht, NodeInfo}
import im.tox.core.error.CoreError
import im.tox.core.io.IO
import im.tox.core.typesafe.Security

import scalaz.\/

/**
 * Base class for handlers that receive a [[DhtUnencryptedPacket]]'s payload.
 */
abstract class DhtUnencryptedPayloadHandler[T, S <: Security](val module: ModuleCompanion[T, S]) {

  def apply(dht: Dht, sender: NodeInfo, packet: T, pingId: Long): CoreError \/ IO[Dht]

}
