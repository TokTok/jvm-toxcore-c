package im.tox.tox4j.core

import im.tox.core.typesafe.VariableSizeByteArrayCompanion

final class ToxFilename private (val value: Array[Byte]) extends AnyVal

object ToxFilename extends VariableSizeByteArrayCompanion[ToxFilename](ToxCoreConstants.MaxFilenameLength) {

  override def unsafeFromByteArray(value: Array[Byte]): ToxFilename = new ToxFilename(value)
  override def toByteArray(self: ToxFilename): Array[Byte] = self.value

}
