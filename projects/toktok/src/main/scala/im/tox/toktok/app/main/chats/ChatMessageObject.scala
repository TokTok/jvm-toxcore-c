package im.tox.toktok.app.main.chats

import im.tox.toktok.app.{Friend, Group}

sealed trait ChatMessageObject

final case class FriendMessageObject(
  friend: Friend,
  lastMessage: String
) extends ChatMessageObject

final case class GroupMessageObject(
  group: Group,
  lastMessage: String
) extends ChatMessageObject

object ChatMessageObject {
  val loremMessage = FriendMessageObject(Friend.lorem, "Hello, how are you?")
  val johnMessage = FriendMessageObject(Friend.john, "Hey buddy, how's things?")
  val groupMessage = GroupMessageObject(Group.group, "Let's Go!")

  def messageType(message: ChatMessageObject): Int = {
    message match {
      case _: FriendMessageObject => 0
      case _: GroupMessageObject  => 1
    }
  }
}
