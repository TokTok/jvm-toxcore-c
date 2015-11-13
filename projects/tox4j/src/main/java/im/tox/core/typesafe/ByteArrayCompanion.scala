package im.tox.core.typesafe

import java.nio.charset.Charset

import scodec.bits.ByteVector
import scodec.{Attempt, Codec, Err}

abstract class ByteArrayCompanion[T <: AnyVal, S <: Security](byteVectorCodec: Codec[ByteVector])
    extends WrappedValueCompanion[Array[Byte], T, S] {

  private final val UTF_8 = Charset.forName("UTF-8")

  final override val codec = byteVectorCodec.exmap[T](
    { bytes => Attempt.fromOption(fromValue(bytes.toArray), new Err.General(s"Validation failed for $this")) },
    { self => Attempt.successful(ByteVector.view(toValue(self))) }
  )

  def fromString(value: String): Option[T] = {
    fromValue(value.getBytes(UTF_8))
  }

}
