package im.tox.core

import im.tox.core.crypto.PlainText
import im.tox.core.error.CoreError
import im.tox.core.typesafe.Security
import scodec.Codec
import scodec.bits.{BitVector, ByteVector}

import scalaz.\/

object ModuleCompanion {
  private val noneValue = None
  private val someValue = Some(())
}

abstract class ModuleCompanion[T, +S <: Security] extends Security.EvidenceCompanion[S] {

  protected final def require(condition: Boolean): Option[Unit] = {
    if (!condition) {
      ModuleCompanion.noneValue
    } else {
      ModuleCompanion.someValue
    }
  }

  def codec: Codec[T]

  final def toBytes(self: T): CoreError \/ PlainText[S] = {
    CoreError(codec.encode(self).map { bits =>
      assert(bits.size % java.lang.Byte.SIZE == 0)
      PlainText(bits.toByteVector)
    })
  }

  final def fromBits(bits: BitVector): CoreError \/ T = {
    CoreError(codec.decode(bits).map(_.value))
  }

  final def fromBytes(bytes: ByteVector): CoreError \/ T = {
    fromBits(bytes.toBitVector)
  }

  final override def toString: String = {
    getClass.getSimpleName
  }

}
