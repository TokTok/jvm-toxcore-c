package im.tox.core.typesafe

abstract class BoundedIntCompanion[T <: AnyVal](val MinValue: Int, val MaxValue: Int) extends IntCompanion[T] {

  def unsafeFromInt(value: Int): T

  final override def fromInt(value: Int): Option[T] = {
    if (MinValue <= value && value <= MaxValue) {
      Some(unsafeFromInt(value))
    } else {
      None
    }
  }

}
