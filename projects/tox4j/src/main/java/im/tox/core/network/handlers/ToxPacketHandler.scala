package im.tox.core.network.handlers

import java.net.InetSocketAddress

import im.tox.core.ModuleCompanion
import im.tox.core.dht.Dht
import im.tox.core.error.CoreError
import im.tox.core.io.IO

import scalaz.\/

abstract class ToxPacketHandler[T](val module: ModuleCompanion[T]) {

  def apply(dht: Dht, origin: InetSocketAddress, packet: T): CoreError \/ IO[Dht]

}
