package im.tox.core.crypto

import im.tox.tox4j.crypto.ToxCryptoConstants

final case class SharedKey private[crypto] (data: Array[Byte]) extends AnyVal {

  override def toString: String = {
    getClass.getSimpleName + "(" + data.map(c => f"$c%02X").mkString + ")"
  }

}

object SharedKey {

  val Size = ToxCryptoConstants.SharedKeyLength

}
