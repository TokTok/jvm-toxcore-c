package im.tox.core

import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.PropertyChecks

import scalaz.{-\/, \/-}

abstract class ModuleCompanionTest[T](module: ModuleCompanion[T]) extends FunSuite with PropertyChecks {

  implicit def arbT: Arbitrary[T]

  test("serialisation and deserialisation") {
    forAll { (value: T) =>
      module.fromBytes(module.toBytes(value)) match {
        case -\/(error)   => fail(error.toString)
        case \/-(decoded) => assert(decoded == value)
      }
    }
  }

}
