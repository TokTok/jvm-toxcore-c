package im.tox.core.typesafe

abstract class DiscreteValueCompanion[Repr, T <: AnyVal](
    protected val values: Repr*
) extends WrappedValueCompanion[Repr, T, Security.Sensitive] {

  final override def validate: Validator = super.validate(values.contains(_))

}
