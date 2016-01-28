package im.tox.core.typesafe

import java.io.DataOutput

import im.tox.core.ModuleCompanion

abstract class ByteArrayCompanion[T <: AnyVal] extends ModuleCompanion[T] {

  protected def unsafeFromByteArray(value: Array[Byte]): T

  def toByteArray(self: T): Array[Byte]
  def fromByteArray(value: Array[Byte]): Option[T]

  final override def write(self: T, packetData: DataOutput): Unit = {
    packetData.write(toByteArray(self))
  }

}
