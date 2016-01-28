package im.tox.core.typesafe

abstract class DiscreteIntCompanion[T <: AnyVal](values: Int*) extends IntCompanion[T] {

  def unsafeFromInt(value: Int): T

  final override def fromInt(value: Int): Option[T] = {
    if (values.contains(value)) {
      Some(unsafeFromInt(value))
    } else {
      None
    }
  }

}
