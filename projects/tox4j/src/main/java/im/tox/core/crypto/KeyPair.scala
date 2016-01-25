package im.tox.core.crypto

final case class KeyPair private[crypto] (
  publicKey: PublicKey,
  secretKey: SecretKey
)
