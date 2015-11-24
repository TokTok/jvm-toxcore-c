package im.tox.tox4j.core

import im.tox.core.typesafe.VariableSizeByteArrayCompanion

final class ToxFilename private (val value: Array[Byte]) extends AnyVal {
  override def toString: String = new String(value)
}

object ToxFilename extends VariableSizeByteArrayCompanion[ToxFilename](ToxCoreConstants.MaxFilenameLength) {

  override def unsafeFromValue(value: Array[Byte]): ToxFilename = new ToxFilename(value)
  override def toValue(self: ToxFilename): Array[Byte] = self.value

}
