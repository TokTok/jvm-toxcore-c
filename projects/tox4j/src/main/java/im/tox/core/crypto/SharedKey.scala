package im.tox.core.crypto

import im.tox.core.typesafe.{KeyCompanion, Security}
import im.tox.tox4j.crypto.ToxCryptoConstants

final case class SharedKey private[crypto] (value: Array[Byte]) extends AnyVal {
  def toHexString: String = SharedKey.toHexString(this)
  override def toString: String = s"$productPrefix($toHexString)"
}

case object SharedKey extends KeyCompanion[SharedKey, Security.Sensitive](
  ToxCryptoConstants.SharedKeyLength,
  _.value
) {

  protected def unsafeFromValue(value: Array[Byte]): SharedKey = new SharedKey(value)

}
