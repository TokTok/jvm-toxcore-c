package im.tox.core.typesafe

import im.tox.core.ModuleCompanion
import scodec.codecs._
import scodec.{Codec, Attempt, Err}

import scala.collection.immutable.TreeSet

abstract class EnumModuleCompanion[T, +S <: Security](private[typesafe] val valueCodec: Codec[Int]) extends ModuleCompanion[T, S] {

  def ordinal(self: T): Int
  def values: TreeSet[T]

  final implicit val ordT: Ordering[T] = Ordering.by(ordinal)

  final override val codec = valueCodec.exmap[T](
    { id => Attempt.fromOption(values.find(x => ordinal(x) == id), new Err.General(s"Invalid $this id: $id")) },
    { self => Attempt.successful(ordinal(self)) }
  )

}
