package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.testing.autotest.AutoTestSuite

final class FriendTypingCallbackTest extends AutoTestSuite {

  type S = Map[ToxFriendNumber, Boolean]

  object Handler extends EventListener(Map.empty) {

    override def friendTyping(friendNumber: ToxFriendNumber, isTyping: Boolean)(state0: State): State = {
      debug(state0, s"friend ${state0.id(friendNumber)} typing state: $isTyping")
      val state = state0.modify(_ + (friendNumber -> isTyping))
      state0.get.get(friendNumber) match {
        case None =>
          assert(!isTyping)
          state.addTask { (tox, av, state) =>
            if (state.id(friendNumber) == state.id.next) {
              debug(state, s"we start typing to ${state.id(friendNumber)}")
              tox.setTyping(friendNumber, typing = true)
            }
            state
          }

        case Some(false) =>
          assert(isTyping)
          if (state.id(friendNumber) == state.id.prev) {
            // id-1 started typing to us, now we also start typing to them.
            state.addTask { (tox, av, state) =>
              debug(state, s"we start typing back to ${state.id(friendNumber)}")
              tox.setTyping(friendNumber, typing = true)
              state
            }
          } else {
            assert(state.id(friendNumber) == state.id.next)
            // id+1 started typing back, so we stop typing.
            state.addTask { (tox, av, state) =>
              debug(state, s"we stop typing to ${state.id(friendNumber)}")
              tox.setTyping(friendNumber, typing = false)
              state
            }
          }

        case Some(true) =>
          assert(!isTyping)
          if (state.id(friendNumber) == state.id.prev) {
            state.addTask { (tox, av, state) =>
              debug(state, s"we also stop typing to ${state.id(friendNumber)}")
              tox.setTyping(friendNumber, typing = false)
              state.finish
            }
          } else {
            assert(state.id(friendNumber) == state.id.next)
            state.finish
          }
      }
    }
  }

}
