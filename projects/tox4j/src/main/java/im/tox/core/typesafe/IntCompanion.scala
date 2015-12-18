package im.tox.core.typesafe

import im.tox.core.ModuleCompanion
import scodec.codecs._
import scodec.{Attempt, Codec, Err}

abstract class IntCompanion[T <: AnyVal](valueCodec: Codec[Int] = int32) extends ModuleCompanion[T, Security.Sensitive] {

  final override val codec = valueCodec.exmap[T](
    { value => Attempt.fromOption(fromInt(value), new Err.General("Validation failed for " + this)) },
    { self => Attempt.successful(toInt(self)) }
  )

  private[typesafe] def fromInt(value: Int): Option[T]
  private[typesafe] def toInt(self: T): Int

}
