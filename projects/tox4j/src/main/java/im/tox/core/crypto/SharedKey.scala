package im.tox.core.crypto

import im.tox.tox4j.crypto.ToxCryptoConstants

final case class SharedKey private[crypto] (value: Array[Byte]) extends AnyVal {
  def readable: String = PublicKey.toString(value)
  override def toString: String = {
    s"${getClass.getSimpleName}($readable)"
  }
}

object SharedKey {

  val Size = ToxCryptoConstants.SharedKeyLength

}
