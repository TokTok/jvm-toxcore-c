package im.tox.tox4j.core.data

import im.tox.core.typesafe.VariableSizeByteArrayCompanion
import im.tox.tox4j.core.ToxCoreConstants

final case class ToxStatusMessage private (value: Array[Byte]) extends AnyVal {
  override def toString: String = new String(value)
}

case object ToxStatusMessage extends VariableSizeByteArrayCompanion[ToxStatusMessage](
  ToxCoreConstants.MaxStatusMessageLength,
  _.value
) {

  override def unsafeFromValue(value: Array[Byte]): ToxStatusMessage = new ToxStatusMessage(value)

}
