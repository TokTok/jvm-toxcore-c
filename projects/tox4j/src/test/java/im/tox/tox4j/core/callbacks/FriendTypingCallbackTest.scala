package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.testing.autotest.{AliceBobTest, AliceBobTestBase}

final class FriendTypingCallbackTest extends AliceBobTest {

  override type State = Boolean
  override def initialState: State = true

  protected override def newChatClient(name: String, expectedFriendName: String) = new ChatClient(name, expectedFriendName) {

    private def setTyping(state: ChatState)(friendNumber: Int, isTyping: Boolean): ChatState = {
      state.addTask { (tox, av, state) =>
        tox.setTyping(friendNumber, isTyping)
        state
      }
    }

    override def friendConnectionStatus(friendNumber: Int, connectionStatus: ToxConnection)(state: ChatState): ChatState = {
      super.friendConnectionStatus(friendNumber, connectionStatus)(state)
      if (connectionStatus != ToxConnection.NONE && isAlice) {
        setTyping(state)(friendNumber, isTyping = true)
      } else {
        state
      }
    }

    override def friendTyping(friendNumber: Int, isTyping: Boolean)(state: ChatState): ChatState = {
      if (state.get) {
        assert(!isTyping)
        state.set(false)
      } else {
        if (isTyping) {
          debug("friend is now typing")
        } else {
          debug("friend stopped typing")
        }
        assert(friendNumber == AliceBobTestBase.FriendNumber)
        if (isBob) {
          if (isTyping) {
            setTyping(state)(friendNumber, isTyping = true)
          } else {
            setTyping(state)(friendNumber, isTyping = false)
              .finish
          }
        } else {
          if (isTyping) {
            setTyping(state)(friendNumber, isTyping = false)
              .finish
          } else {
            state
          }
        }
      }
    }
  }

}
