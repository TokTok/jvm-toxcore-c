package im.tox.core

import im.tox.core.crypto.PlainText.Conversions._
import im.tox.core.typesafe.Security
import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.PropertyChecks

abstract class ModuleCompanionTest[T, S <: Security](module: ModuleCompanion[T, S]) extends FunSuite with PropertyChecks {

  implicit def arbT: Arbitrary[T]

  test("serialisation and deserialisation") {
    forAll { (value: T) =>
      val decoded = module
        .fromBits(
          module.toBytes(value).map(_.unsafeIgnoreSecurity.toBitVector)
            .getOrElse(fail("Encoding failed"))
        ).getOrElse(fail("Decoding failed"))
      assert(decoded == value)
    }
  }

}
