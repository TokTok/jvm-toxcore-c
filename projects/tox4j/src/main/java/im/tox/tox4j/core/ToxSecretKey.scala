package im.tox.tox4j.core

import im.tox.core.typesafe.{KeyCompanion, Security}
import im.tox.tox4j.crypto.ToxCryptoConstants

final case class ToxSecretKey private (value: Array[Byte]) extends AnyVal {
  def readable: String = ToxSecretKey.toString(value)
  override def toString: String = {
    s"${getClass.getSimpleName}($readable)"
  }
}

object ToxSecretKey extends KeyCompanion[ToxSecretKey, Security.Sensitive](ToxCryptoConstants.SecretKeyLength) {

  override def unsafeFromValue(value: Array[Byte]): ToxSecretKey = new ToxSecretKey(value)
  override def toValue(self: ToxSecretKey): Array[Byte] = self.value

}
