package im.tox.core.network.handlers

import java.net.InetSocketAddress

import im.tox.core.ModuleCompanion
import im.tox.core.dht.Dht
import im.tox.core.error.DecoderError
import im.tox.core.io.IO

import scalaz.\/

abstract class ToxPacketHandler[T](val module: ModuleCompanion[T]) {

  def apply(dht: Dht, origin: InetSocketAddress, packet: T): DecoderError \/ IO[Dht]

}
