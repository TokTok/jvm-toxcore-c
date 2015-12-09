package im.tox.core.typesafe

import im.tox.core.ModuleCompanionTest
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import scodec.bits.BitVector

abstract class EnumModuleCompanionTest[T, S <: Security](module: EnumModuleCompanion[T, S]) extends ModuleCompanionTest(module) {

  final override val genInvalidBits: Option[Gen[BitVector]] = Some {
    arbitrary[BitVector].filter(_.sizeGreaterThanOrEqual(module.valueCodec.sizeBound.exact.get)).map { bits =>
      module.valueCodec.decodeValue(bits).getOrElse(fail(s"Unable to generate an arbitrary Int from $bits"))
    }.filter(id => !module.values.exists(module.ordinal(_) == id)).map { id =>
      module.valueCodec.encode(id).getOrElse(fail(s"Unable to encode $id"))
    }
  }

  test("non-empty enum values") {
    assert(module.values.nonEmpty)
  }

  test("distinct ordinals") {
    val ordinals = module.values.toSeq.map(module.ordinal)
    assert(ordinals == ordinals.distinct)
  }

}
