package im.tox.core.dht

import im.tox.core.typesafe.EnumModuleCompanionTest
import org.scalacheck.{Arbitrary, Gen}

object AddressFamilyTest {

  implicit val arbAddressFamily: Arbitrary[AddressFamily] = Arbitrary(
    Gen.oneOf(AddressFamily.values.toSeq)
  )

}

final class AddressFamilyTest extends EnumModuleCompanionTest(AddressFamily) {

  override implicit def arbT: Arbitrary[AddressFamily] = AddressFamilyTest.arbAddressFamily

  test("serialisation of some manually specified values") {
    // Because I don't trust sealedInstancesOf.
    testSerialisation(AddressFamily.Inet4)
    testSerialisation(AddressFamily.Inet6)
  }

}
