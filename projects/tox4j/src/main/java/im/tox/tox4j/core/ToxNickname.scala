package im.tox.tox4j.core

import im.tox.core.typesafe.VariableSizeByteArrayCompanion

final class ToxNickname private (val value: Array[Byte]) extends AnyVal

object ToxNickname extends VariableSizeByteArrayCompanion[ToxNickname](ToxCoreConstants.MaxNameLength) {

  override def unsafeFromByteArray(value: Array[Byte]): ToxNickname = new ToxNickname(value)
  override def toByteArray(self: ToxNickname): Array[Byte] = self.value

}
