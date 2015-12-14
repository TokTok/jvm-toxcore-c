package im.tox.tox4j.av.data

import im.tox.core.typesafe.BoundedIntCompanion

final case class BitRate private (value: Int) extends AnyVal

case object BitRate extends BoundedIntCompanion[BitRate](-1, Int.MaxValue) {

  val Unchanged = new BitRate(-1)
  val Disabled = new BitRate(0)

  override def unsafeFromInt(value: Int): BitRate = new BitRate(value)
  override def toInt(self: BitRate): Int = self.value

}
