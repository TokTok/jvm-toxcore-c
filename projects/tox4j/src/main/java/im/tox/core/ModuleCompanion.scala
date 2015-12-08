package im.tox.core

import im.tox.core.crypto.PlainText
import im.tox.core.error.CoreError
import im.tox.core.typesafe.Security
import scodec.Codec
import scodec.bits.{BitVector, ByteVector}

import scalaz.\/

abstract class ModuleCompanion[T, +S <: Security] {

  def codec: Codec[T]

  final def equals(a: T, b: T): Boolean = {
    toBytes(a) == toBytes(b)
  }

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

}
