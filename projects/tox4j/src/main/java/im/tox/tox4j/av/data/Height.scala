package im.tox.tox4j.av.data

import im.tox.core.typesafe.BoundedIntCompanion

final case class Height private (value: Int) extends AnyVal

case object Height extends BoundedIntCompanion[Height](1, 1200) { // scalastyle:ignore magic.number

  def unsafeFromInt(value: Int): Height = new Height(value)
  def toInt(self: Height): Int = self.value

}
