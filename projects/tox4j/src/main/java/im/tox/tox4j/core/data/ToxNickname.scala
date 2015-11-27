package im.tox.tox4j.core.data

import im.tox.core.typesafe.VariableSizeByteArrayCompanion
import im.tox.tox4j.core.ToxCoreConstants

final case class ToxNickname private (value: Array[Byte]) extends AnyVal {
  override def toString: String = new String(value)
}

case object ToxNickname extends VariableSizeByteArrayCompanion[ToxNickname](
  ToxCoreConstants.MaxNameLength,
  _.value
) {

  override def unsafeFromValue(value: Array[Byte]): ToxNickname = new ToxNickname(value)

}
