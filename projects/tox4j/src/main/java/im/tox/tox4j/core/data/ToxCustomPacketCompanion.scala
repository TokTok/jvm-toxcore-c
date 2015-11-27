package im.tox.tox4j.core.data

import im.tox.core.error.CoreError
import im.tox.core.typesafe.VariableSizeByteArrayCompanion
import im.tox.tox4j.core.ToxCoreConstants

import scalaz.\/

abstract class ToxCustomPacketCompanion[T <: AnyVal](
    val MinPacketId: Int,
    val MaxPacketId: Int,
    toValue: T => Array[Byte]
) extends VariableSizeByteArrayCompanion[T](ToxCoreConstants.MaxCustomPacketSize, toValue) {

  require(MinPacketId <= MaxPacketId)

  override def validate: Validator = super.validate { value =>
    if (value.isEmpty) {
      Some(CoreError.InvalidFormat("Empty custom packet"))
    } else {
      val packetId = value(0) & 0xff
      if (!(MinPacketId to MaxPacketId contains packetId)) {
        Some(CoreError.InvalidFormat(s"Invalid packet id: $packetId; should be $MinPacketId <= id <= $MaxPacketId"))
      } else {
        None
      }
    }
  }

  final def fromByteArray(packetId: Int, value: Array[Byte]): CoreError \/ T = {
    fromValue(packetId.toByte +: value)
  }

}
