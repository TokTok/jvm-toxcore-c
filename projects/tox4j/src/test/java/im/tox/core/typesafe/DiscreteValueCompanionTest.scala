package im.tox.core.typesafe

import im.tox.tox4j.testing.GetDisjunction._
import org.scalacheck.{Gen, Arbitrary}

abstract class DiscreteValueCompanionTest[Repr, T <: AnyVal](module: DiscreteValueCompanion[Repr, T])
    extends WrappedValueCompanionTest(module) {

  override protected def arbT = Arbitrary(Gen.oneOf(module.values).map(module.fromValue(_).get))

}
