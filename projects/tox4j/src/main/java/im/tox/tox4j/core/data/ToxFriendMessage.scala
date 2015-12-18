package im.tox.tox4j.core.data

import im.tox.core.error.CoreError
import im.tox.core.typesafe.VariableSizeByteArrayCompanion
import im.tox.tox4j.core.ToxCoreConstants

final case class ToxFriendMessage private (value: Array[Byte]) extends AnyVal {
  override def toString: String = new String(value)
}

case object ToxFriendMessage extends VariableSizeByteArrayCompanion[ToxFriendMessage](
  ToxCoreConstants.MaxMessageLength,
  _.value
) {

  override protected def validate: Validator = super.validate { value =>
    if (value.isEmpty) {
      Some(CoreError.InvalidFormat("Empty friend message"))
    } else {
      None
    }
  }

  override def unsafeFromValue(value: Array[Byte]): ToxFriendMessage = new ToxFriendMessage(value)

}
