package im.tox.core.typesafe

import im.tox.core.ModuleCompanion

abstract class ByteArrayCompanion[T <: AnyVal, S <: Security] extends ModuleCompanion[T, S] {

  protected def unsafeFromByteArray(value: Array[Byte]): T

  def toByteArray(self: T): Array[Byte]
  def fromByteArray(value: Array[Byte]): Option[T]

}
