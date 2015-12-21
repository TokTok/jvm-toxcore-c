package im.tox.toktok.app

import android.graphics.Color

import scala.collection.mutable.ListBuffer

final case class Group(
  groupName: String,
  friendsList: ListBuffer[Friend],
  primaryColor: Int,
  statusColor: Int
)

object Group {
  val group = Group(
    " \uD83C\uDF20 The Amazing Group",
    ListBuffer(
      Friend.lorem,
      Friend.john
    ),
    Color.parseColor("#9B9B9B"),
    Color.parseColor("#5A5A5A")
  )
}
