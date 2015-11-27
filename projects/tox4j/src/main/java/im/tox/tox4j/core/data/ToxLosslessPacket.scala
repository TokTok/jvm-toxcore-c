package im.tox.tox4j.core.data

final case class ToxLosslessPacket private (value: Array[Byte]) extends AnyVal

case object ToxLosslessPacket extends ToxCustomPacketCompanion[ToxLosslessPacket](
  MinPacketId = 160, // scalastyle:ignore magic.number
  MaxPacketId = 191, // scalastyle:ignore magic.number
  _.value
) {

  override def unsafeFromValue(value: Array[Byte]): ToxLosslessPacket = new ToxLosslessPacket(value)

}
