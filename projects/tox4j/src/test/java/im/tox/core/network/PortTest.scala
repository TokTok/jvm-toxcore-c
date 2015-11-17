package im.tox.core.network

import im.tox.core.ModuleCompanionTest
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary

object PortTest {

  implicit val arbPort: Arbitrary[Port] =
    Arbitrary(arbitrary[Char].filter(_ != 0).map(x => new Port(x)))

}

final class PortTest extends ModuleCompanionTest(Port) {

  override val arbT = PortTest.arbPort

  test("creation") {
    for (portNumber <- Port.MinValue to Port.MaxValue) {
      Port.fromInt(portNumber) match {
        case None       => fail("out of range: " + portNumber)
        case Some(port) => assert(port.value == portNumber)
      }
    }
  }

  test("creation from invalid values") {
    for (portNumber <- Seq(-1, Int.MinValue, Int.MaxValue, 0x10000)) {
      assert(Port.fromInt(portNumber).isEmpty)
    }
  }

}
