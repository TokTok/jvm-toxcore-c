package im.tox.core

import im.tox.core.error.CoreError
import scodec.Codec
import scodec.bits.{BitVector, ByteVector}

import scalaz.\/

object ModuleCompanion {
  private val noneValue = None
  private val someValue = Some(())
}

abstract class ModuleCompanion[T] {

  protected final def require(condition: Boolean): Option[Unit] = {
    if (!condition) {
      ModuleCompanion.noneValue
    } else {
      ModuleCompanion.someValue
    }
  }

  def codec: Codec[T]

  final def toBytes(self: T): CoreError \/ ByteVector = {
    CoreError(codec.encode(self).map { bits =>
      assert(bits.size % java.lang.Byte.SIZE == 0)
      bits.toByteVector
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
