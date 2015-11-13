package im.tox.tox4j.core

final class ToxLosslessPacket private (val value: Array[Byte]) extends AnyVal

object ToxLosslessPacket extends ToxCustomPacketCompanion[ToxLosslessPacket](
  MinPacketId = 160, // scalastyle:ignore magic.number
  MaxPacketId = 191 // scalastyle:ignore magic.number
) {

  override def unsafeFromValue(value: Array[Byte]): ToxLosslessPacket = new ToxLosslessPacket(value)
  override def toValue(self: ToxLosslessPacket): Array[Byte] = self.value

}
