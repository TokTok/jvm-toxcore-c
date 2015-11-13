package im.tox.tox4j.core

import im.tox.core.typesafe.VariableSizeByteArrayCompanion

final class ToxFriendMessage private (val value: Array[Byte]) extends AnyVal

object ToxFriendMessage extends VariableSizeByteArrayCompanion[ToxFriendMessage](ToxCoreConstants.MaxMessageLength) {

  override def validate: Validator = super.validate { value =>
    value.nonEmpty
  }

  override def unsafeFromValue(value: Array[Byte]): ToxFriendMessage = new ToxFriendMessage(value)
  override def toValue(self: ToxFriendMessage): Array[Byte] = self.value

}
