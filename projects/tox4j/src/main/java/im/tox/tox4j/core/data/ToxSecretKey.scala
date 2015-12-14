package im.tox.tox4j.core.data

import im.tox.core.typesafe.{KeyCompanion, Security}
import im.tox.tox4j.crypto.ToxCryptoConstants

final case class ToxSecretKey private (value: Array[Byte]) extends AnyVal {
  def toHexString: String = ToxSecretKey.toHexString(this)
  override def toString: String = {
    s"${getClass.getSimpleName}($toHexString)"
  }
}

case object ToxSecretKey extends KeyCompanion[ToxSecretKey, Security.Sensitive](
  ToxCryptoConstants.SecretKeyLength,
  _.value
) {

  override def unsafeFromValue(value: Array[Byte]): ToxSecretKey = new ToxSecretKey(value)

}
