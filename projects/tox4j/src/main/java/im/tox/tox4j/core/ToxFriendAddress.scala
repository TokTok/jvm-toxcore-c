package im.tox.tox4j.core

import im.tox.core.typesafe.{KeyCompanion, Security}

final case class ToxFriendAddress private (value: Array[Byte]) extends AnyVal {
  def readable: String = ToxFriendAddress.toString(value)
  override def toString: String = {
    s"${getClass.getSimpleName}($readable)"
  }
}

object ToxFriendAddress extends KeyCompanion[ToxFriendAddress, Security.Sensitive](ToxCoreConstants.AddressSize) {

  override def unsafeFromValue(value: Array[Byte]): ToxFriendAddress = new ToxFriendAddress(value)
  override def toValue(self: ToxFriendAddress): Array[Byte] = self.value

}
