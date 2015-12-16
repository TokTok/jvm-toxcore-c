package im.tox.tox4j.core.data

import im.tox.core.typesafe.{KeyCompanion, Security}
import im.tox.tox4j.core.ToxCoreConstants

final case class ToxFriendAddress private (value: Array[Byte]) extends AnyVal {
  def toHexString: String = ToxFriendAddress.toHexString(this)
  override def toString: String = s"$productPrefix($toHexString)"
}

case object ToxFriendAddress extends KeyCompanion[ToxFriendAddress, Security.Sensitive](
  ToxCoreConstants.AddressSize,
  _.value
) {

  override def unsafeFromValue(value: Array[Byte]): ToxFriendAddress = new ToxFriendAddress(value)

}
