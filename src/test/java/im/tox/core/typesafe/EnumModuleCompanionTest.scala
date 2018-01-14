package im.tox.core.typesafe

import im.tox.core.ModuleCompanionTest
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen

@SuppressWarnings(Array("org.wartremover.warts.Equals"))
abstract class EnumModuleCompanionTest[T, S <: Security](module: EnumModuleCompanion[T, S]) extends ModuleCompanionTest(module) {

  test("non-empty enum values") {
    assert(module.values.nonEmpty)
  }

  test("distinct ordinals") {
    val ordinals = module.values.toSeq.map(module.ordinal)
    assert(ordinals == ordinals.distinct)
  }

}
