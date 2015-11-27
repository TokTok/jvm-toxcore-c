package im.tox.core.crypto

import im.tox.core.typesafe.KeyCompanionTest
import org.scalacheck.{Arbitrary, Gen}

object NonceTest {

  implicit val arbNonce: Arbitrary[Nonce] =
    Arbitrary(Gen.const(()).map(_ => Nonce.random()))

}

final class NonceTest extends KeyCompanionTest(Nonce)(NonceTest.arbNonce)
