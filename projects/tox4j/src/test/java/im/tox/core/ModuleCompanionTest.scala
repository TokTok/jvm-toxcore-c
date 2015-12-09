package im.tox.core

import im.tox.core.crypto.PlainText.Conversions._
import im.tox.core.error.CoreError
import im.tox.core.typesafe.Security
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.PropertyChecks
import scodec.Err
import scodec.bits.BitVector

import scalaz.-\/

abstract class ModuleCompanionTest[T, S <: Security](module: ModuleCompanion[T, S]) extends FunSuite with PropertyChecks {

  def genInvalidBits: Option[Gen[BitVector]] = None

  implicit def arbT: Arbitrary[T]
  implicit val arbBitVector: Arbitrary[BitVector] = Arbitrary(arbitrary[Array[Byte]].map(BitVector.view))

  protected def testSerialisation(value: T): Unit = {
    val decoded = module
      .fromBits(
        module.toBytes(value).map(_.unsafeIgnoreSecurity.toBitVector)
          .getOrElse(fail("Encoding failed"))
      ).getOrElse(fail("Decoding failed"))
    assert(module.equals(decoded, value))
  }

  test("encoding and decoding of valid values") {
    forAll { (value: T) =>
      testSerialisation(value)
    }
  }

  test("decoding empty bit vectors") {
    if (module.nullable) {
      assert(module.fromBits(BitVector.empty).isRight)
    } else {
      module.fromBits(BitVector.empty) match {
        case -\/(CoreError.CodecError(Err.InsufficientBits(needed, have, context))) =>
          assert(have == 0)
        case unexpected =>
          fail(s"Expected ${Err.InsufficientBits} but got $unexpected")
      }
    }
  }

  test("decoding of invalid bit vectors") {
    genInvalidBits.foreach { genInvalidBits =>
      forAll(genInvalidBits) { invalid =>
        assert(module.fromBits(invalid).isLeft)
      }
    }
  }

}
