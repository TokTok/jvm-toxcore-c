package im.tox.tox4j.core

import im.tox.core.typesafe.VariableSizeByteArrayCompanion

final class ToxFriendMessage private (val value: Array[Byte]) extends AnyVal

object ToxFriendMessage extends VariableSizeByteArrayCompanion[ToxFriendMessage](ToxCoreConstants.MaxMessageLength) {

  override def validate(value: Array[Byte]): Boolean = value.nonEmpty

  override def unsafeFromByteArray(value: Array[Byte]): ToxFriendMessage = new ToxFriendMessage(value)
  override def toByteArray(self: ToxFriendMessage): Array[Byte] = self.value

}
