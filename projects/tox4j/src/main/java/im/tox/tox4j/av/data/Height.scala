package im.tox.tox4j.av.data

import im.tox.core.typesafe.BoundedIntCompanion

final case class Height private (value: Int) extends AnyVal

case object Height extends BoundedIntCompanion[Height](20, 1200) { // scalastyle:ignore magic.number

  override def unsafeFromInt(value: Int): Height = new Height(value)
  override def toInt(self: Height): Int = self.value

}
