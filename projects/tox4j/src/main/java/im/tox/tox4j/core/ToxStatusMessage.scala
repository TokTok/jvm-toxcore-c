package im.tox.tox4j.core

import im.tox.core.typesafe.VariableSizeByteArrayCompanion

final class ToxStatusMessage private (val value: Array[Byte]) extends AnyVal {
  override def toString: String = new String(value)
}

object ToxStatusMessage extends VariableSizeByteArrayCompanion[ToxStatusMessage](ToxCoreConstants.MaxStatusMessageLength) {

  override def unsafeFromValue(value: Array[Byte]): ToxStatusMessage = new ToxStatusMessage(value)
  override def toValue(self: ToxStatusMessage): Array[Byte] = self.value

}
