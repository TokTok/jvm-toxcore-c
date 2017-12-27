package im.tox.core.typesafe

import org.scalacheck.Gen

abstract class BoundedIntCompanionTest[T <: AnyVal](module: BoundedIntCompanion[T]) extends IntCompanionTest(module) {

  override protected final def genValidInt = Gen.choose(module.MinValue, module.MaxValue)
  override protected final def genInvalidInt = {
    val tooSmallGen = if (module.MinValue > Int.MinValue) {
      Some(Gen.choose(Int.MinValue, module.MinValue - 1))
    } else {
      None
    }
    val tooLargeGen = if (module.MaxValue < Int.MaxValue) {
      Some(Gen.choose(module.MaxValue + 1, Int.MaxValue))
    } else {
      None
    }

    tooSmallGen.toList ++ tooLargeGen.toList match {
      case Nil                  => fail(s"$module is a bounded int companion but sets no bounds")
      case List(gen)            => gen
      case gen1 :: gen2 :: tail => Gen.oneOf(gen1, gen2, tail: _*)
    }
  }

}
