package im.tox.tox4j.core.data

import im.tox.core.typesafe.KeyCompanionTest
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

object ToxFriendAddressTest {

  implicit val arbToxFriendAddress: Arbitrary[ToxFriendAddress] = {
    Arbitrary(Gen.containerOfN[Array, Byte](ToxFriendAddress.Size, arbitrary[Byte]).map(ToxFriendAddress.unsafeFromValue))
  }

}

final class ToxFriendAddressTest extends KeyCompanionTest(ToxFriendAddress)(ToxFriendAddressTest.arbToxFriendAddress)
