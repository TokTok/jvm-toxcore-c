package im.tox.core.typesafe

import scodec.codecs._

abstract class VariableSizeByteArrayCompanion[T <: AnyVal](val MaxSize: Int)
    extends ByteArrayCompanion[T, Security.Sensitive](variableSizeBytes(uint16, bytes)) {

  override def validate: Validator = super.validate { value =>
    value.length <= MaxSize
  }

}
