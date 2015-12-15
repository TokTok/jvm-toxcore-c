package im.tox.tox4j.core.data

import im.tox.core.typesafe.BoundedIntCompanion

final case class ToxFriendNumber private (value: Int) extends AnyVal

case object ToxFriendNumber extends BoundedIntCompanion[ToxFriendNumber](0, Int.MaxValue) {

  override def unsafeFromInt(value: Int): ToxFriendNumber = new ToxFriendNumber(value)
  override def toInt(self: ToxFriendNumber): Int = self.value

  implicit val ordToxFriendNumber: Ordering[ToxFriendNumber] = Ordering.by(_.value)

}
