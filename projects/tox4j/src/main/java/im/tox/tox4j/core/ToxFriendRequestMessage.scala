package im.tox.tox4j.core

import im.tox.core.typesafe.VariableSizeByteArrayCompanion

final class ToxFriendRequestMessage private (val value: Array[Byte]) extends AnyVal

object ToxFriendRequestMessage extends VariableSizeByteArrayCompanion[ToxFriendRequestMessage](ToxCoreConstants.MaxFriendRequestLength) {

  override def unsafeFromByteArray(value: Array[Byte]): ToxFriendRequestMessage = new ToxFriendRequestMessage(value)
  override def toByteArray(self: ToxFriendRequestMessage): Array[Byte] = self.value

}
