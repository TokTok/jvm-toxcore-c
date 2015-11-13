package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.ToxFriendMessage
import im.tox.tox4j.core.enums.{ToxConnection, ToxMessageType}
import im.tox.tox4j.testing.autotest.{AliceBobTest, AliceBobTestBase}

final class FriendMessageCallbackTest extends AliceBobTest {

  override type State = Unit
  override def initialState: State = ()

  protected override def newChatClient(name: String, expectedFriendName: String) = new ChatClient(name, expectedFriendName) {

    override def friendConnectionStatus(friendNumber: Int, connectionStatus: ToxConnection)(state: ChatState): ChatState = {
      super.friendConnectionStatus(friendNumber, connectionStatus)(state)
      if (connectionStatus != ToxConnection.NONE) {
        state.addTask { (tox, state) =>
          tox.friendSendMessage(friendNumber, ToxMessageType.NORMAL, 0,
            ToxFriendMessage.fromValue(s"My name is $selfName".getBytes).get)
          state
        }
      } else {
        state
      }
    }

    override def friendMessage(
      friendNumber: Int,
      newType: ToxMessageType,
      timeDelta: Int,
      message: ToxFriendMessage
    )(
      state: ChatState
    ): ChatState = {
      debug("received a message: " + new String(message.value))
      assert(friendNumber == AliceBobTestBase.FriendNumber)
      assert(newType == ToxMessageType.NORMAL)
      assert(timeDelta >= 0)
      assert(new String(message.value) == s"My name is $expectedFriendName")
      state.finish
    }
  }

}
