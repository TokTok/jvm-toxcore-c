package im.tox.core.network

import im.tox.core.typesafe.BoundedIntCompanion
import scodec.codecs._

/**
 * IP_Port stores an IP datastructure with a port.
 */
final class Port private[network] (val value: Int) extends AnyVal

object Port extends BoundedIntCompanion[Port](1, 0xffff, uint16) { // scalastyle:ignore magic.number

  def unsafeFromInt(value: Int): Port = new Port(value)
  def toInt(self: Port): Int = self.value

}
