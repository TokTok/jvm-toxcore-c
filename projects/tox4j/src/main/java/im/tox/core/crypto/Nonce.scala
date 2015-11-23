package im.tox.core.crypto

import im.tox.core.random.RandomCore
import im.tox.core.typesafe.{FixedSizeByteArrayCompanion, Security}
import im.tox.tox4j.crypto.ToxCryptoConstants

final case class Nonce private[crypto] (data: Seq[Byte]) extends AnyVal {
  override def toString: String = {
    "Nonce(" + data.map(c => f"$c%02X").mkString + ")"
  }
}

object Nonce extends FixedSizeByteArrayCompanion[Nonce, Security.NonSensitive](ToxCryptoConstants.NonceLength) {

  override protected def unsafeFromValue(value: Array[Byte]): Nonce = new Nonce(value)
  override def toValue(self: Nonce): Array[Byte] = self.data.toArray

  /**
   * The random nonce generation function is used everywhere in toxcore to
   * generate nonces. It uses the cryptographically secure random number generator
   * in toxcore which prevents new nonces from being associated with previous
   * nonces which could lead to issues in places like the onion module. If many
   * different packets could be tied together due to how the nonces were generated
   * using rand for example, it might lead to tying DHT and onion announce packets
   * together which would introduce a flaw in the system as non friends could tie
   * some peoples DHT and long term keys together.
   */
  def random(): Nonce = {
    /**
     * Nonces used for crypto_box are 24 bytes.
     */
    Nonce(RandomCore.randomBytes(Size))
  }

}
