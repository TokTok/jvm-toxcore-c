package im.tox.core.typesafe

import java.nio.charset.Charset

import im.tox.core.ModuleCompanion

abstract class ByteArrayCompanion[T <: AnyVal, S <: Security] extends ModuleCompanion[T, S] {

  private final val UTF_8 = Charset.forName("UTF-8")

  protected def unsafeFromByteArray(value: Array[Byte]): T

  def toByteArray(self: T): Array[Byte]
  def fromByteArray(value: Array[Byte]): Option[T]

  def fromString(value: String): Option[T] = {
    fromByteArray(value.getBytes(UTF_8))
  }

}
