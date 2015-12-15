package im.tox.tox4j.av.callbacks

import im.tox.tox4j.av.data.BitRate
import im.tox.tox4j.av.exceptions.ToxavCallException
import im.tox.tox4j.core.data.{ToxFriendNumber, ToxFriendMessage}
import im.tox.tox4j.core.enums.{ToxConnection, ToxMessageType}
import im.tox.tox4j.testing.GetDisjunction._
import im.tox.tox4j.testing.ToxExceptionChecks
import im.tox.tox4j.testing.autotest.AutoTestSuite

final class CallCallbackTest extends AutoTestSuite with ToxExceptionChecks {

  type S = Unit

  object Handler extends EventListener(()) {

    override def friendConnectionStatus(
      friendNumber: ToxFriendNumber,
      connectionStatus: ToxConnection
    )(state0: State): State = {
      val state = super.friendConnectionStatus(friendNumber, connectionStatus)(state0)

      if (connectionStatus == ToxConnection.NONE || state.id(friendNumber) != state.id.next) {
        state
      } else {
        // Call id+1.
        state.addTask { (tox, av, state) =>
          av.call(friendNumber, BitRate.fromInt(10).get, BitRate.Disabled)
          state
        }
      }
    }

    override def call(friendNumber: ToxFriendNumber, audioEnabled: Boolean, videoEnabled: Boolean)(state: State): State = {
      if (state.id(friendNumber) == state.id.prev) {
        state.addTask { (tox, av, state) =>
          // Calling them back while they are ringing is invalid.
          intercept(ToxavCallException.Code.FRIEND_ALREADY_IN_CALL) {
            av.call(friendNumber, BitRate.fromInt(10).get, BitRate.Disabled)
          }
          // Say "No thanks" and stop talking.
          debug(state, s"Got a call from ${state.id(friendNumber)}; rejecting")
          tox.friendSendMessage(friendNumber, ToxMessageType.NORMAL, 0, ToxFriendMessage.fromString("No thanks").get)
          state.finish
        }
      } else {
        fail(s"I shouldn't have been called by friend ${state.id(friendNumber)}")
        state
      }
    }

    override def friendMessage(
      friendNumber: ToxFriendNumber,
      messageType: ToxMessageType,
      timeDelta: Int,
      message: ToxFriendMessage
    )(state: State): State = {
      assert(message.toString == "No thanks")
      debug(state, "Aww... I got rejected :( oh well")
      state.finish
    }

  }

}
