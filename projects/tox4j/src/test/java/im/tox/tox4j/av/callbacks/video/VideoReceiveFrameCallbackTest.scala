package im.tox.tox4j.av.callbacks.video

import java.util

import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.av.data._
import im.tox.tox4j.av.enums.ToxavFriendCallState
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.testing.ToxExceptionChecks
import im.tox.tox4j.testing.autotest.AutoTestSuite
import im.tox.tox4j.testing.autotest.AutoTestSuite.timed

final class VideoReceiveFrameCallbackTest extends AutoTestSuite with ToxExceptionChecks {

  private val video = VideoGenerators.Selected

  /**
   * The time to wait for the next frame. Increase this if you need more time
   * to look at the displayed images.
   */
  private val frameDelay = 0

  type S = Int

  object Handler extends EventListener(0) {

    private val displayImage = {
      if (sys.env.contains("TRAVIS")) {
        None
      } else {
        if (video.height * video.width <= 100 * 40) {
          Some(ConsoleVideoDisplay(video.width, video.height))
        } else {
          Some(GuiVideoDisplay(video.width, video.height))
        }
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
          av.call(friendNumber, BitRate.Disabled, BitRate.fromInt(8000).get)
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

      val (generationTime, (y, u, v)) = timed {
        video.yuv(state0.get)
      }
      assert(y.length == video.width * video.height)
      assert(u.length == video.width * video.height / 4)
      assert(v.length == video.width * video.height / 4)

      val (displayTime, ()) = timed {
        displayImage.foreach { display =>
          display.displaySent(state0.get, y, u, v)
        }
      }
      val (sendTime, ()) = timed {
        av.videoSendFrame(friendNumber, video.width, video.height, y, u, v)
      }

      debug(
        state,
        s"Sent frame ${state0.get}: generationTime=${generationTime}ms, displayTime=${displayTime}ms, sendTime=${sendTime}ms"
      )

      if (state.get >= video.length) {
        state.finish
      } else {
        state.addTask(frameDelay)(sendFrame(friendNumber))
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

      val times = displayImage.flatMap { display =>
        display.displayReceived(state0.get, y, u, v, yStride, uStride, vStride)
      }.map {
        case (parseTime, displayTime) =>
          s", parseTime=${parseTime}ms, displayTime=${displayTime}ms"
      }.getOrElse("")

      debug(state, s"Received frame ${state0.get}: size=($width, $height), strides=($yStride, $uStride, $vStride)$times")

      if (state.get >= video.length) {
        state.finish
      } else {
        state
      }
    }

  }

}
