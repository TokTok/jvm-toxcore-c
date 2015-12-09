package im.tox.core.crypto

import org.scalacheck.{Arbitrary, Gen}

object KeyPairTest {

  implicit val arbKeyPair: Arbitrary[KeyPair] =
    Arbitrary(Gen.resultOf[Unit, KeyPair](_ => CryptoCore.keyPair()))

}
