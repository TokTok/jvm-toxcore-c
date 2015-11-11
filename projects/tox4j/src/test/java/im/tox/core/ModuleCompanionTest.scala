package im.tox.core

import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.PropertyChecks

abstract class ModuleCompanionTest[T](module: ModuleCompanion[T]) extends FunSuite with PropertyChecks {

  implicit def arbT: Arbitrary[T]

  test("serialisation and deserialisation") {
    forAll { (value: T) =>
      val decoded = module
        .fromBits(
          module.toBytes(value).map(_.toBitVector)
            .getOrElse(fail("Encoding failed"))
        ).getOrElse(fail("Decoding failed"))
      assert(decoded == value)
    }
  }

}
