package im.tox.core.typesafe

import scodec.Codec
import scodec.codecs._

abstract class BoundedIntCompanion[T <: AnyVal](
    val MinValue: Int,
    val MaxValue: Int,
    valueCodec: Codec[Int] = int32
) extends IntCompanion[T](valueCodec) {

  def unsafeFromInt(value: Int): T

  final override def fromInt(value: Int): Option[T] = {
    if (MinValue <= value && value <= MaxValue) {
      Some(unsafeFromInt(value))
    } else {
      None
    }
  }

}
