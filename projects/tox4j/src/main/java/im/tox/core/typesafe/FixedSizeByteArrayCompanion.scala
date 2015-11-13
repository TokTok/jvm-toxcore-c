package im.tox.core.typesafe

import scodec.codecs._

abstract class FixedSizeByteArrayCompanion[T <: AnyVal, S <: Security](val Size: Int)
    extends ByteArrayCompanion[T, S](bytes(Size)) {

  override def validate: Validator = super.validate { value =>
    value.length == Size
  }

}
