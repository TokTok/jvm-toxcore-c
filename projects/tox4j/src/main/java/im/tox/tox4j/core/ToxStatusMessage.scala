package im.tox.tox4j.core

import im.tox.core.typesafe.VariableSizeByteArrayCompanion

final class ToxStatusMessage private (val value: Array[Byte]) extends AnyVal

object ToxStatusMessage extends VariableSizeByteArrayCompanion[ToxStatusMessage](ToxCoreConstants.MaxStatusMessageLength) {

  override def unsafeFromByteArray(value: Array[Byte]): ToxStatusMessage = new ToxStatusMessage(value)
  override def toByteArray(self: ToxStatusMessage): Array[Byte] = self.value

}
