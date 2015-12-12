package im.tox.tox4j.av.callbacks

import java.util

import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.av.data._
import im.tox.tox4j.av.enums.ToxavFriendCallState
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.testing.ToxExceptionChecks
import im.tox.tox4j.testing.autotest.AutoTestSuite

final class VideoReceiveFrameCallbackTest extends AutoTestSuite with ToxExceptionChecks {

  type S = Int

  object Handler extends EventListener(0) {

    override def friendConnectionStatus(
      friendNumber: Int,
      connectionStatus: ToxConnection
    )(state0: State): State = {
      val state = super.friendConnectionStatus(friendNumber, connectionStatus)(state0)

      if (connectionStatus == ToxConnection.NONE || state.id(friendNumber) != state.id.next) {
        state
      } else {
        // Call id+1.
        state.addTask { (tox, av, state) =>
          debug(state, s"Ringing ${state.id(friendNumber)}")
          av.call(friendNumber, BitRate.Disabled, BitRate.fromInt(320).get)
          state
        }
      }
    }

    override def call(friendNumber: Int, audioEnabled: Boolean, videoEnabled: Boolean)(state: State): State = {
      if (state.id(friendNumber) == state.id.prev) {
        state.addTask { (tox, av, state) =>
          debug(state, s"Got a call from ${state.id(friendNumber)}; accepting")
          av.answer(friendNumber, BitRate.Disabled, BitRate.Disabled)
          state
        }
      } else {
        fail(s"I shouldn't have been called by friend ${state.id(friendNumber)}")
        state
      }
    }

    private def sendFrame(friendNumber: Int)(tox: ToxCore[State], av: ToxAv[State], state0: State): State = {
      val state = state0.modify(_ + 1)

      val y = Array.ofDim[Byte](100 * 100)
      val u = Array.ofDim[Byte](50 * 50)
      val v = Array.ofDim[Byte](50 * 50)
      av.videoSendFrame(friendNumber, 100, 100, y, u, v)

      if (state.get >= 200) {
        state.finish
      } else {
        state.addTask(sendFrame(friendNumber))
      }
    }

    override def callState(friendNumber: Int, callState: util.Collection[ToxavFriendCallState])(state: State): State = {
      debug(state, s"Call with ${state.id(friendNumber)} is now $callState")
      state.addTask(sendFrame(friendNumber))
    }

    override def videoReceiveFrame(
      friendNumber: Int,
      width: Int, height: Int,
      y: Array[Byte], u: Array[Byte], v: Array[Byte],
      yStride: Int, uStride: Int, vStride: Int
    )(state0: State): State = {
      val state = state0.modify(_ + 1)
      debug(state, "Received video frame")

      if (state.get >= 200) {
        state.finish
      } else {
        state
      }
    }

  }

}
