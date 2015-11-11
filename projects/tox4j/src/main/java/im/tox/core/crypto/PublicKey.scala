package im.tox.core.crypto

import im.tox.core.error.CoreError
import im.tox.core.typesafe.FixedSizeByteArrayCompanion
import im.tox.tox4j.crypto.ToxCryptoConstants

import scalaz.{-\/, \/, \/-}

final class PublicKey private[crypto] (val value: Seq[Byte]) extends AnyVal {
  override def toString: String = {
    getClass.getSimpleName + "(" + value.map(c => f"$c%02X").mkString + ")"
  }
}

object PublicKey extends FixedSizeByteArrayCompanion[PublicKey](ToxCryptoConstants.PublicKeyLength) {

  protected def unsafeFromByteArray(value: Array[Byte]): PublicKey = new PublicKey(value)

  def toByteArray(self: PublicKey): Array[Byte] = self.value.toArray

  def fromString(string: String): CoreError \/ PublicKey = {
    val bytes = parsePublicKey(string)
    if (bytes.length != Size) {
      -\/(CoreError.InvalidFormat(s"Invalid size of public key (${bytes.length} != $Size)"))
    } else {
      \/-(new PublicKey(bytes))
    }
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
