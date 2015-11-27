package im.tox.tox4j.core.data

final class ToxLossyPacket private (val value: Array[Byte]) extends AnyVal

case object ToxLossyPacket extends ToxCustomPacketCompanion[ToxLossyPacket](
  MinPacketId = 200, // scalastyle:ignore magic.number
  MaxPacketId = 254, // scalastyle:ignore magic.number
  _.value
) {

  override def unsafeFromValue(value: Array[Byte]): ToxLossyPacket = new ToxLossyPacket(value)

}
