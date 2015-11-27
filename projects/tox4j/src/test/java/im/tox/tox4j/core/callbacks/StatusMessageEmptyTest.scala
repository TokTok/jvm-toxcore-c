package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxStatusMessage
import im.tox.tox4j.testing.autotest.{AliceBobTest, AliceBobTestBase}

final class StatusMessageEmptyTest extends AliceBobTest {

  override type State = Int
  override def initialState: State = 0

  protected override def newChatClient(name: String, expectedFriendName: String) = new ChatClient(name, expectedFriendName) {

    override def friendStatusMessage(friendNumber: Int, message: ToxStatusMessage)(state: ChatState): ChatState = {
      debug(s"friend changed status message to: ${new String(message.value)}")
      assert(friendNumber == AliceBobTestBase.FriendNumber)
      state.get match {
        case 0 =>
          val nextState = state.set(1)
          assert(message.value.isEmpty)
          if (isAlice) {
            nextState.addTask { (tox, state) =>
              tox.setStatusMessage(ToxStatusMessage.fromString("One").get)
              state
            }
          } else {
            nextState
          }

        case 1 =>
          val nextState = state.set(2)
          if (isAlice) {
            assert(new String(message.value) == "Two")
            nextState.addTask { (tox, state) =>
              tox.setStatusMessage(ToxStatusMessage.fromString("").get)
              state
            }
          } else {
            assert(new String(message.value) == "One")
            nextState.addTask { (tox, state) =>
              tox.setStatusMessage(ToxStatusMessage.fromString("Two").get)
              state
            }
          }

        case 2 =>
          val nextState = state.finish
          assert(message.value.isEmpty)
          if (isBob) {
            nextState.addTask { (tox, state) =>
              tox.setStatusMessage(ToxStatusMessage.fromString("").get)
              state
            }
          } else {
            nextState
          }
      }
    }
  }

}
