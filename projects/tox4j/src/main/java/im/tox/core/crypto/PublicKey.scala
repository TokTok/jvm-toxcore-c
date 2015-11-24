package im.tox.core.crypto

import im.tox.core.typesafe.{KeyCompanion, Security}
import im.tox.tox4j.crypto.ToxCryptoConstants

final case class PublicKey private[crypto] (value: Seq[Byte]) extends AnyVal {
  def readable: String = PublicKey.toString(value)
  override def toString: String = {
    s"${getClass.getSimpleName}($readable)"
  }
}

object PublicKey extends KeyCompanion[PublicKey, Security.NonSensitive](ToxCryptoConstants.PublicKeyLength) {

  protected def unsafeFromValue(value: Array[Byte]): PublicKey = new PublicKey(value)
  def toValue(self: PublicKey): Array[Byte] = self.value.toArray

}
