package im.tox.core.typesafe

import im.tox.core.ModuleCompanion
import im.tox.core.error.CoreError

import scalaz.\/

abstract class WrappedValueCompanion[Repr, T <: AnyVal, S <: Security](
    toValue: T => Repr
) extends ModuleCompanion[T, S] {

  sealed trait Validator { self =>

    protected[typesafe] def apply(repr: Repr): Option[CoreError]

    def apply(f: Repr => Option[CoreError]): Validator = {
      new Validator {
        override def apply(value: Repr): Option[CoreError] = {
          self.apply(value).orElse(f(value))
        }
      }
    }

  }

  protected object Validator extends Validator {

    protected[typesafe] override def apply(array: Repr): Option[CoreError] = None

    def require(condition: Boolean, message: => String): Option[CoreError] = {
      CoreError.require(condition, message).swap.toOption
    }

  }

  protected def validate: Validator = Validator

  protected def unsafeFromValue(value: Repr): T

  final def fromValue(value: Repr): CoreError \/ T = {
    \/.fromEither(validate(value).toLeft(unsafeFromValue(value)))
  }

}
