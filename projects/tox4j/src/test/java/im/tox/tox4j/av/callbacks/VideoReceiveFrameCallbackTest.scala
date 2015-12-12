package im.tox.tox4j.av.callbacks

import java.io.{DataOutputStream, FileOutputStream}
import java.util

import _root_.im.tox.tox4j.av.ToxAv
import _root_.im.tox.tox4j.av.data._
import _root_.im.tox.tox4j.av.enums.ToxavFriendCallState
import _root_.im.tox.tox4j.core.ToxCore
import _root_.im.tox.tox4j.core.enums.ToxConnection
import _root_.im.tox.tox4j.testing.ToxExceptionChecks
import _root_.im.tox.tox4j.testing.autotest.AutoTestSuite

final class VideoReceiveFrameCallbackTest extends AutoTestSuite with ToxExceptionChecks {

  private def video = VideoGenerator.Selected

  type S = Int

  object Handler extends EventListener(0) {

    private val displayImage = {
      if (sys.env.contains("TRAVIS")) {
        None
      } else {
        Some(
          // VideoDisplay.Gui(video.width, video.height)
          VideoDisplay.Console(video.width, video.height)
        )
      }
    }

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
          av.call(friendNumber, BitRate.Disabled, BitRate.fromInt(18000).get)
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
      debug(state, s"Sending video frame ${state0.get}")

      val y = video.y(state0.get)
      val u = video.u(state0.get)
      val v = video.v(state0.get)
      assert(y.length == video.width * video.height)
      assert(u.length == video.width * video.height / 4)
      assert(v.length == video.width * video.height / 4)

      av.videoSendFrame(friendNumber, video.width, video.height, y, u, v)

      if (state.get >= video.length) {
        state.finish
      } else {
        val delay =
          if (video.width * video.height < 100 * 100) {
            // Delay the next frame until the iteration after the next if the image is
            // very small. Otherwise, toxav will reduce the iteration interval to 0.
            1
          } else {
            0
          }
        state.addTask(delay)(sendFrame(friendNumber))
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
      debug(state, s"Received video frame ${state0.get}: size = ($width, $height), strides = ($yStride, $uStride, $vStride)")

      displayImage.foreach { display =>
        display.display(y, u, v, yStride, uStride, vStride)
      }

      if (state.get >= video.length) {
        state.finish
      } else {
        state
      }
    }

  }

}
