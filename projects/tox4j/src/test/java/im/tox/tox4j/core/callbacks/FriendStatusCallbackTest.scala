package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.enums.ToxUserStatus
import im.tox.tox4j.testing.autotest.AutoTestSuite

final class FriendStatusCallbackTest extends AutoTestSuite {

  type S = ToxUserStatus

  object Handler extends EventListener(ToxUserStatus.NONE) {

    private def go(status: ToxUserStatus)(state: State): State = {
      state.addTask { (tox, av, state) =>
        tox.setStatus(status)
        state.put(status)
      }
    }

    override def friendStatus(friendNumber: ToxFriendNumber, status: ToxUserStatus)(state: State): State = {
      debug(state, s"friend changed status to: $status")

      val isAlice = state.friendList(friendNumber) == state.id.next
      val isBob = state.friendList(friendNumber) == state.id.prev

      state.get match {
        case ToxUserStatus.NONE =>
          if (isAlice) {
            assert(status == ToxUserStatus.NONE)
            go(ToxUserStatus.AWAY)(state)
          } else {
            if (status != ToxUserStatus.NONE) {
              assert(status == ToxUserStatus.AWAY)
              go(ToxUserStatus.BUSY)(state)
            } else {
              state
            }
          }

        case selfStatus =>
          if (isAlice && selfStatus == ToxUserStatus.AWAY) {
            assert(status == ToxUserStatus.BUSY)
            go(ToxUserStatus.NONE)(state)
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
