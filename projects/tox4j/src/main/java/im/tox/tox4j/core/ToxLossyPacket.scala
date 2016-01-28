package im.tox.tox4j.core

final class ToxLossyPacket private (val value: Array[Byte]) extends AnyVal

object ToxLossyPacket extends ToxCustomPacketCompanion[ToxLossyPacket](
  MinPacketId = 200, // scalastyle:ignore magic.number
  MaxPacketId = 254 // scalastyle:ignore magic.number
) {

  override def unsafeFromByteArray(value: Array[Byte]): ToxLossyPacket = new ToxLossyPacket(value)
  override def toByteArray(self: ToxLossyPacket): Array[Byte] = self.value

}
