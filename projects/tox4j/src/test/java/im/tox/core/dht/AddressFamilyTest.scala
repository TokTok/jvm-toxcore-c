package im.tox.core.dht

import im.tox.core.ModuleCompanionTest
import org.scalacheck.{Arbitrary, Gen}

object AddressFamilyTest {

  implicit val arbAddressFamily: Arbitrary[AddressFamily] = Arbitrary(
    Gen.oneOf(AddressFamily.values.toSeq)
  )

}

final class AddressFamilyTest extends ModuleCompanionTest(AddressFamily) {

  override implicit def arbT: Arbitrary[AddressFamily] = AddressFamilyTest.arbAddressFamily

  test("other") {
    testSerialisation(AddressFamily.Inet4)
  }

}
