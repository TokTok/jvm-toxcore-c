package im.tox.core.typesafe

import im.tox.core.ModuleCompanionTest
import im.tox.tox4j.testing.GetDisjunction._
import org.scalacheck.{Arbitrary, Gen}

abstract class KeyCompanionTest[T <: AnyVal, S <: Security](
    companion: KeyCompanion[T, S]
)(implicit final val arbT: Arbitrary[T]) extends ModuleCompanionTest[T, S](companion) {

  test("fromHexString") {
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

  test("toString") {
    forAll { (self: T) =>
      assert(companion.equals(companion.fromHexString(self.toString).get, self))
    }
  }

  test("toHexString") {
    forAll { (self: T) =>
      assert(companion.equals(companion.fromHexString(companion.toHexString(self)).get, self))
    }
  }

  test("optimised toHexString") {
    forAll { (self: T) =>
      assert(companion.toHexStringOpt(self) == companion.toHexStringRef(self))
    }
  }

}
