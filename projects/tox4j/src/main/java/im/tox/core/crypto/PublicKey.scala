package im.tox.core.crypto

import im.tox.core.typesafe.{FixedSizeByteArrayCompanion, Security}
import im.tox.tox4j.crypto.ToxCryptoConstants

final class PublicKey private[crypto] (val value: Seq[Byte]) extends AnyVal {
  override def toString: String = {
    getClass.getSimpleName + "(" + value.map(c => f"$c%02X").mkString + ")"
  }
}

object PublicKey extends FixedSizeByteArrayCompanion[PublicKey, Security.NonSensitive](ToxCryptoConstants.PublicKeyLength) {

  protected def unsafeFromValue(value: Array[Byte]): PublicKey = new PublicKey(value)

  def toValue(self: PublicKey): Array[Byte] = self.value.toArray

  override def fromString(string: String): Option[PublicKey] = {
    fromValue(parsePublicKey(string))
  }

  private def parsePublicKey(id: String): Array[Byte] = {
    val publicKey = new Array[Byte](id.length / 2)
    publicKey.indices foreach { i =>
      publicKey(i) =
        ((fromHexDigit(id.charAt(i * 2)) << 4) +
          fromHexDigit(id.charAt(i * 2 + 1))).toByte
    }
    publicKey
  }

  private def fromHexDigit(c: Char): Byte = {
    val digit =
      if (false) { 0 }
      else if ('0' to '9' contains c) { c - '0' }
      else if ('A' to 'F' contains c) { c - 'A' + 10 }
      else if ('a' to 'f' contains c) { c - 'a' + 10 }
      else { throw new IllegalArgumentException("Non-hex digit character: " + c) }
    digit.toByte
  }

}
