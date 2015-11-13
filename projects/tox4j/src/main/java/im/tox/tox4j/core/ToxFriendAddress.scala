package im.tox.tox4j.core

import im.tox.core.typesafe.{Security, FixedSizeByteArrayCompanion}

final class ToxFriendAddress private (val value: Array[Byte]) extends AnyVal

object ToxFriendAddress extends FixedSizeByteArrayCompanion[ToxFriendAddress, Security.Sensitive](ToxCoreConstants.AddressSize) {

  override def unsafeFromValue(value: Array[Byte]): ToxFriendAddress = new ToxFriendAddress(value)
  override def toValue(self: ToxFriendAddress): Array[Byte] = self.value

}
