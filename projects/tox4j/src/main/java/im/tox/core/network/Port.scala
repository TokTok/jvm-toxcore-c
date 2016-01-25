package im.tox.core.network

import java.io.{DataInputStream, DataOutput}
import im.tox.core.error.DecoderError
import im.tox.core.typesafe.BoundedIntCompanion

import scalaz.{\/, \/-}

/**
 * IP_Port stores an IP datastructure with a port.
 */
final class Port private[network] (val value: Int) extends AnyVal

object Port extends BoundedIntCompanion[Port](1, 0xffff) { // scalastyle:ignore magic.number

  def unsafeFromInt(value: Int): Port = new Port(value)
  def toInt(self: Port): Int = self.value

  override def write(self: Port, packetData: DataOutput): Unit = {
    packetData.writeShort(self.value.toShort)
  }

  override def read(packet: DataInputStream): DecoderError \/ Port = {
    \/-(new Port(packet.readUnsignedShort()))
  }

}
