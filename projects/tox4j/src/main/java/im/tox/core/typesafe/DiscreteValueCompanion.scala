package im.tox.core.typesafe

import im.tox.core.ModuleCompanion

abstract class DiscreteValueCompanion[T <: AnyVal, U](unsafeFromValue: U => T)(val values: U*)
    extends ModuleCompanion[T, Security.Sensitive] {

  final def fromValue(value: U): Option[T] = {
    if (values.contains(value)) {
      Some(unsafeFromValue(value))
    } else {
      None
    }
  }

}
