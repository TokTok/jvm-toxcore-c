package im.tox.core

import com.typesafe.scalalogging.Logger
import im.tox.core.crypto.PlainText
import im.tox.core.error.CoreError
import im.tox.core.typesafe.Security
import org.slf4j.LoggerFactory
import scodec.Codec
import scodec.bits.{BitVector, ByteVector}

import scalaz.\/

abstract class ModuleCompanion[T, +S <: Security] {

  protected final val logger = Logger(LoggerFactory.getLogger(getClass))

  def codec: Codec[T]

  final def equals(a: T, b: T): Boolean = {
    toBytes(a) == toBytes(b)
  }

  final def toBytes(self: T): CoreError \/ PlainText[S] = {
    CoreError(codec.encode(self).map { bits =>
      if (bits.size % java.lang.Byte.SIZE != 0) {
        logger.warn(s"Codec for $this does not produce byte-aligned output")
      }
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
