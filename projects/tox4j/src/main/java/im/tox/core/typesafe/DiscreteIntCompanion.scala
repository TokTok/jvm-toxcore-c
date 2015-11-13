package im.tox.core.typesafe

abstract class DiscreteIntCompanion[T <: AnyVal](
    protected val values: Int*
) extends IntCompanion[T] {

  protected def unsafeFromInt(value: Int): T

  final override def fromInt(value: Int): Option[T] = {
    if (values.contains(value)) {
      Some(unsafeFromInt(value))
    } else {
      None
    }
  }

}
