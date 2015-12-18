package im.tox.core.typesafe

import scodec.codecs._

abstract class VariableSizeByteArrayCompanion[T <: AnyVal](
    val MaxSize: Int,
    toValue: T => Array[Byte]
) extends ByteArrayCompanion[T, Security.Sensitive](variableSizeBytes(uint16, bytes), toValue) {

  override protected def validate: Validator = super.validate { value =>
    Validator.require(value.length <= MaxSize, s"Invalid length: ${value.length} > $MaxSize")
  }

}
