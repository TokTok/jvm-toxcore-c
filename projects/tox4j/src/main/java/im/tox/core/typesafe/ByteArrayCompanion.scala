package im.tox.core.typesafe

import im.tox.core.ModuleCompanion

abstract class ByteArrayCompanion[T <: AnyVal] extends ModuleCompanion[T] {

  protected def unsafeFromByteArray(value: Array[Byte]): T

  def toByteArray(self: T): Array[Byte]
  def fromByteArray(value: Array[Byte]): Option[T]

}
