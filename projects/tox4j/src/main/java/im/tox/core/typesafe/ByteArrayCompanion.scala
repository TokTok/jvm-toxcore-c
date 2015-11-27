package im.tox.core.typesafe

import java.nio.charset.Charset

import im.tox.core.error.CoreError
import scodec.bits.ByteVector
import scodec.{Attempt, Codec}

import scalaz.\/

abstract class ByteArrayCompanion[T <: AnyVal, S <: Security](
    byteVectorCodec: Codec[ByteVector],
    toValue: T => Array[Byte]
) extends WrappedValueCompanion[Array[Byte], T, S](toValue) {

  private final val UTF_8 = Charset.forName("UTF-8")

  final override val codec = byteVectorCodec.exmap[T](
    { bytes => CoreError.toAttempt(fromValue(bytes.toArray)) },
    { self => Attempt.successful(ByteVector.view(toValue(self))) }
  )

  final def fromString(value: String): CoreError \/ T = {
    fromValue(value.getBytes(UTF_8))
  }

}
