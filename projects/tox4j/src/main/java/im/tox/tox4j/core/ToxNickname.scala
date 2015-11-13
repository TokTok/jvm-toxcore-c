package im.tox.tox4j.core

import im.tox.core.typesafe.VariableSizeByteArrayCompanion

final class ToxNickname private (val value: Array[Byte]) extends AnyVal

object ToxNickname extends VariableSizeByteArrayCompanion[ToxNickname](ToxCoreConstants.MaxNameLength) {

  override def unsafeFromValue(value: Array[Byte]): ToxNickname = new ToxNickname(value)
  override def toValue(self: ToxNickname): Array[Byte] = self.value

}
