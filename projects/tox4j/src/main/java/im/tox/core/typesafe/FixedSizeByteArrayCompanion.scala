package im.tox.core.typesafe

import scodec.codecs._

abstract class FixedSizeByteArrayCompanion[T <: AnyVal, S <: Security](
    val Size: Int,
    toValue: T => Array[Byte]
) extends ByteArrayCompanion[T, S](bytes(Size), toValue) {

  override def validate: Validator = super.validate { value =>
    Validator.require(value.length == Size, s"Invalid length: ${value.length} != $Size")
  }

}
