package im.tox.core.typesafe

import im.tox.core.ModuleCompanionTest

abstract class WrappedValueCompanionTest[Repr, T <: AnyVal, S <: Security](module: WrappedValueCompanion[Repr, T, S])
  extends ModuleCompanionTest[T, S](module)
