package im.tox.tox4j.core.data

import im.tox.core.typesafe.KeyCompanionTest
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

object ToxFileIdTest {

  implicit val arbToxFileId: Arbitrary[ToxFileId] = {
    Arbitrary(Gen.containerOfN[Array, Byte](ToxFileId.Size, arbitrary[Byte]).map(ToxFileId.unsafeFromValue))
  }

}

final class ToxFileIdTest extends KeyCompanionTest(ToxFileId)(ToxFileIdTest.arbToxFileId)
