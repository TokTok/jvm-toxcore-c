package im.tox.core.crypto

import im.tox.core.typesafe.{FixedSizeByteArrayCompanion, Security}
import im.tox.tox4j.crypto.ToxCryptoConstants

final case class SecretKey private[crypto] (value: Seq[Byte]) extends AnyVal {
  override def toString: String = {
    getClass.getSimpleName + "(" + value.map(c => f"$c%02X").mkString + ")"
  }
}

object SecretKey extends FixedSizeByteArrayCompanion[SecretKey, Security.Sensitive](ToxCryptoConstants.SecretKeyLength) {

  protected def unsafeFromValue(value: Array[Byte]): SecretKey = new SecretKey(value)
  def toValue(self: SecretKey): Array[Byte] = self.value.toArray

}
