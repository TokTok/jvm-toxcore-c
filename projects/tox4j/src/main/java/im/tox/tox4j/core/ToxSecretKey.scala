package im.tox.tox4j.core

import im.tox.core.typesafe.{Security, FixedSizeByteArrayCompanion}
import im.tox.tox4j.crypto.ToxCryptoConstants

final class ToxSecretKey private (val value: Array[Byte]) extends AnyVal {
  override def toString: String = {
    getClass.getSimpleName + "(" + value.map(c => f"$c%02X").mkString + ")"
  }
}

object ToxSecretKey extends FixedSizeByteArrayCompanion[ToxSecretKey, Security.Sensitive](ToxCryptoConstants.SecretKeyLength) {

  override def unsafeFromByteArray(value: Array[Byte]): ToxSecretKey = new ToxSecretKey(value)
  override def toByteArray(self: ToxSecretKey): Array[Byte] = self.value

}
