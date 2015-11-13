package im.tox.tox4j.core

import im.tox.core.typesafe.VariableSizeByteArrayCompanion

final class ToxFriendRequestMessage private (val value: Array[Byte]) extends AnyVal

object ToxFriendRequestMessage extends VariableSizeByteArrayCompanion[ToxFriendRequestMessage](ToxCoreConstants.MaxFriendRequestLength) {

  override def unsafeFromValue(value: Array[Byte]): ToxFriendRequestMessage = new ToxFriendRequestMessage(value)
  override def toValue(self: ToxFriendRequestMessage): Array[Byte] = self.value

}
