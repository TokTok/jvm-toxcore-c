package im.tox.core.crypto

import im.tox.core.typesafe.{KeyCompanion, Security}
import im.tox.tox4j.crypto.ToxCryptoConstants

final case class SecretKey private[crypto] (value: IndexedSeq[Byte]) extends AnyVal {
  def toHexString: String = SecretKey.toHexString(this)
  override def toString: String = s"$productPrefix($toHexString)"
}

case object SecretKey extends KeyCompanion[SecretKey, Security.Sensitive](
  ToxCryptoConstants.SecretKeyLength,
  _.value.toArray
) {

  protected def unsafeFromValue(value: Array[Byte]): SecretKey = new SecretKey(value)

}
