package im.tox.core.crypto

import java.io.{DataInputStream, DataOutput}

import im.tox.core.ModuleCompanion
import im.tox.core.error.DecoderError
import im.tox.core.random.RandomCore
import im.tox.tox4j.crypto.ToxCryptoConstants

import scalaz.{\/, \/-}

final case class Nonce private[crypto] (data: Seq[Byte]) extends AnyVal {
  override def toString: String = {
    "Nonce(" + data.map(c => f"$c%02X").mkString + ")"
  }
}

object Nonce extends ModuleCompanion[Nonce] {

  val Size = ToxCryptoConstants.NonceLength

  override def write(self: Nonce, packetData: DataOutput): Unit = {
    packetData.write(self.data.toArray)
  }

  override def read(packetData: DataInputStream): DecoderError \/ Nonce = {
    val data = Array.ofDim[Byte](Size)
    packetData.read(data)
    \/-(Nonce(data))
  }

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
