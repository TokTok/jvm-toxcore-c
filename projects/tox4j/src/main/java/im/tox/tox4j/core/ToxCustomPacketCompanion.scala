package im.tox.tox4j.core

import im.tox.core.typesafe.VariableSizeByteArrayCompanion

abstract class ToxCustomPacketCompanion[T <: AnyVal](
    val MinPacketId: Int,
    val MaxPacketId: Int
) extends VariableSizeByteArrayCompanion[T](ToxCoreConstants.MaxCustomPacketSize) {

  require(MinPacketId <= MaxPacketId)

  override def validate(value: Array[Byte]): Boolean = {
    value.nonEmpty && {
      val firstByte = value(0) & 0xff
      MinPacketId <= firstByte && firstByte <= MaxPacketId
    }
  }

}
