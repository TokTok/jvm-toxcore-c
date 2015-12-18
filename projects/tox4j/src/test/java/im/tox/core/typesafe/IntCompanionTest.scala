package im.tox.core.typesafe

import im.tox.core.ModuleCompanionTest
import org.scalacheck.{Arbitrary, Gen}

abstract class IntCompanionTest[T <: AnyVal](module: IntCompanion[T]) extends ModuleCompanionTest(module) {

  protected def genValidInt: Gen[Int]
  protected def genInvalidInt: Gen[Int]

  override protected final def arbT = Arbitrary(genValidInt.map(module.fromInt(_).get))

  test("fromInt (valid)") {
    forAll(genValidInt) { (int: Int) =>
      assert(module.fromInt(int).isDefined)
    }
  }

  test("fromInt (invalid)") {
    forAll(genInvalidInt) { (int: Int) =>
      assert(module.fromInt(int).isEmpty)
    }
  }

  test("toInt") {
    forAll(genValidInt) { (int: Int) =>
      assert(module.toInt(module.fromInt(int).get) == int)
    }
  }

}
