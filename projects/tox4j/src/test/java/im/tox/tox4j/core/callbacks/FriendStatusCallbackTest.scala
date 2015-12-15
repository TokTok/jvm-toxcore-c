package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.enums.ToxUserStatus
import im.tox.tox4j.testing.autotest.{AliceBobTest, AliceBobTestBase}

final class FriendStatusCallbackTest extends AliceBobTest {

  override type State = ToxUserStatus
  override def initialState: State = ToxUserStatus.NONE

  protected override def newChatClient(name: String, expectedFriendName: String) = new ChatClient(name, expectedFriendName) {

    private def go(state: ChatState)(status: ToxUserStatus): ChatState = {
      state.addTask { (tox, av, state) =>
        tox.setStatus(status)
        state.set(status)
      }
    }

    override def friendStatus(friendNumber: ToxFriendNumber, status: ToxUserStatus)(state: ChatState): ChatState = {
      debug(s"friend changed status to: $status")
      assert(friendNumber == AliceBobTestBase.FriendNumber)

      state.get match {
        case ToxUserStatus.NONE =>
          if (isAlice) {
            assert(status == ToxUserStatus.NONE)
            go(state)(ToxUserStatus.AWAY)
          } else {
            if (status != ToxUserStatus.NONE) {
              assert(status == ToxUserStatus.AWAY)
              go(state)(ToxUserStatus.BUSY)
            } else {
              state
            }
          }

        case selfStatus =>
          if (isAlice && selfStatus == ToxUserStatus.AWAY) {
            assert(status == ToxUserStatus.BUSY)
            go(state)(ToxUserStatus.NONE)
              .finish
          } else if (isBob && selfStatus == ToxUserStatus.BUSY) {
            assert(status == ToxUserStatus.NONE)
            state.finish
          } else {
            state
          }
      }
    }

  }

}
