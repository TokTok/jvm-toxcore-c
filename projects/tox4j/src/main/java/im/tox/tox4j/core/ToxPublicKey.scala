package im.tox.tox4j.core

import im.tox.core.typesafe.{KeyCompanion, Security}
import im.tox.tox4j.crypto.ToxCryptoConstants

final case class ToxPublicKey private (value: Array[Byte]) extends AnyVal {
  def readable: String = ToxPublicKey.toString(value)
  override def toString: String = {
    s"${getClass.getSimpleName}($readable)"
  }
}

object ToxPublicKey extends KeyCompanion[ToxPublicKey, Security.NonSensitive](ToxCryptoConstants.PublicKeyLength) {

  override def unsafeFromValue(value: Array[Byte]): ToxPublicKey = new ToxPublicKey(value)
  override def toValue(self: ToxPublicKey): Array[Byte] = self.value.toArray

}
