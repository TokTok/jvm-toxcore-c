package im.tox.core.typesafe

abstract class DiscreteValueCompanion[Repr, T <: AnyVal](
    toValue: T => Repr,
    protected val values: Repr*
) extends WrappedValueCompanion[Repr, T, Security.Sensitive](toValue) {

  final override def validate: Validator = super.validate { value =>
    Validator.require(values.contains(value), s"Invalid value: $value; accepted values: $values")
  }

}
