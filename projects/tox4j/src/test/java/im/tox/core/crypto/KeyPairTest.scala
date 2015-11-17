package im.tox.core.crypto

import org.scalacheck.{Arbitrary, Gen}

object KeyPairTest {

  def take(keyPair: KeyPair, maxSize: Int): KeyPair = {
    KeyPair(
      PublicKeyTest.take(keyPair.publicKey, maxSize),
      SecretKeyTest.take(keyPair.secretKey, maxSize)
    )
  }

  implicit val arbPublicKey: Arbitrary[KeyPair] =
    Arbitrary(Gen.resultOf[Unit, KeyPair](_ => CryptoCore.keyPair()))

}
