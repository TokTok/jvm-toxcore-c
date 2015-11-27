package im.tox.core.typesafe

import im.tox.core.ModuleCompanionTest
import im.tox.tox4j.testing.GetDisjunction._
import org.scalacheck.{Arbitrary, Gen}

abstract class KeyCompanionTest[T <: AnyVal, S <: Security](
    companion: KeyCompanion[T, S]
)(implicit final val arbT: Arbitrary[T]) extends ModuleCompanionTest[T, S](companion) {

  test("testFromString") {
    forAll(Gen.containerOfN[Array, Char](
      companion.Size * 2,
      Gen.oneOf(
        Gen.choose('0', '9'),
        Gen.choose('a', 'f'),
        Gen.choose('A', 'F')
      )
    ).map(new String(_))) { string =>
      companion.fromHexString(string).get
    }
  }

  test("testToString") {
    forAll { (self: T) =>
      assert(companion.equals(companion.fromHexString(self.toString).get, self))
    }
  }

}
