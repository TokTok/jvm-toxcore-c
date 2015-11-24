package im.tox.core.crypto

import im.tox.core.typesafe.{KeyCompanion, Security}
import im.tox.tox4j.crypto.ToxCryptoConstants

final case class SecretKey private[crypto] (value: Seq[Byte]) extends AnyVal {
  def readable: String = SecretKey.toString(value)
  override def toString: String = {
    s"${getClass.getSimpleName}($readable)"
  }
}

object SecretKey extends KeyCompanion[SecretKey, Security.Sensitive](ToxCryptoConstants.SecretKeyLength) {

  protected def unsafeFromValue(value: Array[Byte]): SecretKey = new SecretKey(value)
  def toValue(self: SecretKey): Array[Byte] = self.value.toArray

}
