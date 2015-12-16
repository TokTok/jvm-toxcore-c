package im.tox.tox4j.av.data

import im.tox.core.typesafe.BoundedIntCompanion

final case class Width private (value: Int) extends AnyVal

case object Width extends BoundedIntCompanion[Width](20, 1920) { // scalastyle:ignore magic.number

  override def unsafeFromInt(value: Int): Width = new Width(value)
  override def toInt(self: Width): Int = self.value

}
