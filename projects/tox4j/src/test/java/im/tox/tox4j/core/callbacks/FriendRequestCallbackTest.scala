package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core._
import im.tox.tox4j.core.data.{ToxFriendRequestMessage, ToxPublicKey}
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.testing.autotest.{AliceBobTest, AliceBobTestBase}

final class FriendRequestCallbackTest extends AliceBobTest {

  override type State = Unit
  override def initialState: State = ()

  protected override def newChatClient(name: String, expectedFriendName: String) = new ChatClient(name, expectedFriendName) {

    override def setup(tox: ToxCore[ChatState])(state: ChatState): ChatState = {
      tox.deleteFriend(AliceBobTestBase.FriendNumber)
      if (isAlice) {
        tox.addFriend(expectedFriendAddress, ToxFriendRequestMessage.fromString(s"Hey this is $selfName").get)
      }
      state
    }

    override def friendConnectionStatus(friendNumber: Int, connectionStatus: ToxConnection)(state: ChatState): ChatState = {
      super.friendConnectionStatus(friendNumber, connectionStatus)(state)
      if (connectionStatus != ToxConnection.NONE) {
        state.finish
      } else {
        state
      }
    }

    override def friendRequest(publicKey: ToxPublicKey, timeDelta: Int, message: ToxFriendRequestMessage)(state: ChatState): ChatState = {
      debug(s"got friend request: ${new String(message.value)}")
      assert(isBob, "Alice shouldn't get a friend request")
      assert(publicKey.value sameElements expectedFriendPublicKey.value)
      assert(timeDelta >= 0)
      assert(new String(message.value) == s"Hey this is $expectedFriendName")
      state.addTask { (tox, av, state) =>
        tox.addFriendNorequest(publicKey)
        state
      }
    }

  }

}
