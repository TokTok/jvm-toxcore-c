package im.tox.tox4j.core.data

import im.tox.core.typesafe.KeyCompanionTest
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

object ToxSecretKeyTest {

  implicit val arbToxSecretKey: Arbitrary[ToxSecretKey] = {
    Arbitrary(Gen.containerOfN[Array, Byte](ToxSecretKey.Size, arbitrary[Byte]).map(ToxSecretKey.unsafeFromValue))
  }

}

final class ToxSecretKeyTest extends KeyCompanionTest(ToxSecretKey)(ToxSecretKeyTest.arbToxSecretKey)
