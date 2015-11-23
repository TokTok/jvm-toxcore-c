package im.tox.tox4j.core

import im.tox.core.typesafe.{FixedSizeByteArrayCompanion, Security}
import im.tox.tox4j.crypto.ToxCryptoConstants

final class ToxPublicKey private (val value: Array[Byte]) extends AnyVal {
  override def toString: String = {
    getClass.getSimpleName + "(" + value.map(c => f"$c%02X").mkString + ")"
  }
}

object ToxPublicKey extends FixedSizeByteArrayCompanion[ToxPublicKey, Security.NonSensitive](ToxCryptoConstants.PublicKeyLength) {

  override def unsafeFromValue(value: Array[Byte]): ToxPublicKey = new ToxPublicKey(value)
  override def toValue(self: ToxPublicKey): Array[Byte] = self.value.toArray

}
