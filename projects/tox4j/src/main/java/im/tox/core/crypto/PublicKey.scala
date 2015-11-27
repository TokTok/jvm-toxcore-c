package im.tox.core.crypto

import im.tox.core.typesafe.{KeyCompanion, Security}
import im.tox.tox4j.crypto.ToxCryptoConstants

final case class PublicKey private[crypto] (value: Seq[Byte]) extends AnyVal {
  def readable: String = PublicKey.toHexString(this)
  override def toString: String = {
    s"${getClass.getSimpleName}($readable)"
  }
}

case object PublicKey extends KeyCompanion[PublicKey, Security.NonSensitive](
  ToxCryptoConstants.PublicKeyLength,
  _.value.toArray
) {

  protected def unsafeFromValue(value: Array[Byte]): PublicKey = new PublicKey(value)

}
