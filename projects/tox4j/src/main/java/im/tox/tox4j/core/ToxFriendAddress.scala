package im.tox.tox4j.core

import im.tox.core.typesafe.FixedSizeByteArrayCompanion

final class ToxFriendAddress private (val value: Array[Byte]) extends AnyVal

object ToxFriendAddress extends FixedSizeByteArrayCompanion[ToxFriendAddress](ToxCoreConstants.AddressSize) {

  override def unsafeFromByteArray(value: Array[Byte]): ToxFriendAddress = new ToxFriendAddress(value)
  override def toByteArray(self: ToxFriendAddress): Array[Byte] = self.value

}
