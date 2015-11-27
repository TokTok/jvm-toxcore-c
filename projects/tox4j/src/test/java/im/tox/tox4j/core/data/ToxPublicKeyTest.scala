package im.tox.tox4j.core.data

import im.tox.core.typesafe.KeyCompanionTest
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

object ToxPublicKeyTest {

  implicit val arbToxPublicKey: Arbitrary[ToxPublicKey] = {
    Arbitrary(Gen.containerOfN[Array, Byte](ToxPublicKey.Size, arbitrary[Byte]).map(ToxPublicKey.unsafeFromValue))
  }

}

final class ToxPublicKeyTest extends KeyCompanionTest(ToxPublicKey)(ToxPublicKeyTest.arbToxPublicKey)
