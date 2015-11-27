package im.tox.tox4j.core.data

import im.tox.core.typesafe.VariableSizeByteArrayCompanion
import im.tox.tox4j.core.ToxCoreConstants

final case class ToxFriendRequestMessage private (value: Array[Byte]) extends AnyVal {
  override def toString: String = new String(value)
}

case object ToxFriendRequestMessage extends VariableSizeByteArrayCompanion[ToxFriendRequestMessage](
  ToxCoreConstants.MaxFriendRequestLength,
  _.value
) {

  override def unsafeFromValue(value: Array[Byte]): ToxFriendRequestMessage = new ToxFriendRequestMessage(value)

}
