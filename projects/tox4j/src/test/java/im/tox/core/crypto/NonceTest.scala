package im.tox.core.crypto

import im.tox.core.ModuleCompanionTest
import org.scalacheck.{Arbitrary, Gen}

object NonceTest {

  implicit val arbNonce: Arbitrary[Nonce] =
    Arbitrary(Gen.const(()).map(_ => Nonce.random()))

}

final class NonceTest extends ModuleCompanionTest(Nonce) {

  override val arbT = NonceTest.arbNonce

}
