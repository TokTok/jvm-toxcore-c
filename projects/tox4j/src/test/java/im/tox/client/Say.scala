package im.tox.client

import im.tox.tox4j.OptimisedIdOps._
import im.tox.tox4j.core.ToxCoreConstants
import im.tox.tox4j.core.data.{ToxFriendMessage, ToxFriendNumber}
import im.tox.tox4j.core.enums.ToxMessageType
import im.tox.tox4j.testing.GetDisjunction._

trait Say {

  protected def say(friendNumber: ToxFriendNumber, message: String)(state: ToxClientState): ToxClientState = {
    val (head, tail) = message.splitAt(ToxCoreConstants.MaxMessageLength)
    if (tail.nonEmpty) {
      state |> say(friendNumber, head) |> say(friendNumber, tail)
    } else {
      state.addTask { (tox, av, state) =>
        tox.friendSendMessage(friendNumber, ToxMessageType.NORMAL, 0, ToxFriendMessage.fromString(head).get)
        state
      }
    }
  }

}
