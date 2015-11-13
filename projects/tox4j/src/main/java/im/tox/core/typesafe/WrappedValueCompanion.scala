package im.tox.core.typesafe

import im.tox.core.ModuleCompanion

abstract class WrappedValueCompanion[Repr, T <: AnyVal, S <: Security] extends ModuleCompanion[T, S] {

  sealed trait Validator extends (Repr => Boolean) { self =>
    def apply(f: Repr => Boolean): Validator = {
      new Validator {
        override def apply(value: Repr): Boolean = self.apply(value) && f(value)
      }
    }
  }

  private object Validator extends Validator {
    def apply(array: Repr): Boolean = true
  }

  protected def validate: Validator = Validator

  protected def unsafeFromValue(value: Repr): T

  final def fromValue(value: Repr): Option[T] = {
    if (validate(value)) {
      Some(unsafeFromValue(value))
    } else {
      None
    }
  }

  protected def toValue(self: T): Repr

}
