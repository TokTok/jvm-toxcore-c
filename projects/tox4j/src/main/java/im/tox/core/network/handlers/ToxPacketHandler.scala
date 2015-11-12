package im.tox.core.network.handlers

import java.net.InetSocketAddress

import im.tox.core.ModuleCompanion
import im.tox.core.dht.Dht
import im.tox.core.error.CoreError
import im.tox.core.io.IO
import im.tox.core.typesafe.Security

import scalaz.\/

abstract class ToxPacketHandler[T, +S <: Security](val module: ModuleCompanion[T, S]) {

  def apply(dht: Dht, origin: InetSocketAddress, packet: T): CoreError \/ IO[Dht]

}
