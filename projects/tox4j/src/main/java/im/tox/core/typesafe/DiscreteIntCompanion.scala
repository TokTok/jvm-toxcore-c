package im.tox.core.typesafe

abstract class DiscreteIntCompanion[T <: AnyVal](
    protected[typesafe] val values: Int*
) extends IntCompanion[T] {

  protected def unsafeFromInt(value: Int): T

  override final def fromInt(value: Int): Option[T] = {
    if (values.contains(value)) {
      Some(unsafeFromInt(value))
    } else {
      None
    }
  }

}
