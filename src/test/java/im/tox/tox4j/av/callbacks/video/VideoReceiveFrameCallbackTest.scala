package im.tox.tox4j.av.callbacks.video

import java.awt.Color
import java.util

import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.av.data._
import im.tox.tox4j.av.enums.ToxavFriendCallState
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.testing.ToxExceptionChecks
import im.tox.tox4j.testing.autotest.AutoTestSuite
import im.tox.tox4j.testing.autotest.AutoTestSuite.timed

@SuppressWarnings(Array("org.wartremover.warts.Equals"))
final class VideoReceiveFrameCallbackTest extends AutoTestSuite with ToxExceptionChecks {

  /**
   * How much of the sent data must be received for the test to pass.
   */
  private val minCompletionRatio = 0.7

  private val video = VideoGenerators.Colors(VideoGenerators.DefaultWidth, VideoGenerators.DefaultHeight)

  private val bitRate = BitRate.fromInt(1).get

  override def maxParticipantCount: Int = 2

  final case class S(t: Int = 0, received: List[Color] = Nil)

  object Handler extends EventListener(S()) {

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
          debug(state, s"Ringing ${state.id(friendNumber)} to send ${video.length} frames")
          av.call(friendNumber, BitRate.Disabled, bitRate)
          state
        }
      }
    }

    override def call(friendNumber: ToxFriendNumber, audioEnabled: Boolean, videoEnabled: Boolean)(state: State): State = {
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

    private def sendFrame(friendNumber: ToxFriendNumber)(tox: ToxCore, av: ToxAv, state0: State): State = {
      val state = state0.modify(s => s.copy(t = s.t + 1))

      val (y, u, v) = video.yuv(state0.get.t)
      assert(y.length == video.size)
      assert(u.length == video.size / 4)
      assert(v.length == video.size / 4)

      val time = timed {
        av.videoSendFrame(friendNumber, video.width.value, video.height.value, y, u, v)
      }
      debug(state, s"Sent frame ${state.get.t} of ${video.length} ($time ms)")

      if (state.get.t >= video.length) {
        state.finish
      } else {
        state.addTask(sendFrame(friendNumber))
      }
    }

    override def callState(friendNumber: ToxFriendNumber, callState: util.EnumSet[ToxavFriendCallState])(state: State): State = {
      debug(state, s"Call with ${state.id(friendNumber)} is now $callState")
      state.addTask(sendFrame(friendNumber))
    }

    private def approximatelyEqual(color1: Color)(color2: Color): Boolean = {
      val rgb1 = color1.getRGBColorComponents(Array.ofDim(3)).map(c => Math.round(c * 32))
      val rgb2 = color2.getRGBColorComponents(Array.ofDim(3)).map(c => Math.round(c * 32))
      (rgb1, rgb2).zipped.forall(_ == _)
    }

    private def average(plane: Array[Byte]): Int = {
      plane.map(_ & 0xff).sum / plane.length
    }

    override def videoReceiveFrame(
      friendNumber: ToxFriendNumber,
      width: Width, height: Height,
      y: Array[Byte], u: Array[Byte], v: Array[Byte],
      yStride: Int, uStride: Int, vStride: Int
    )(state0: State): State = {
      val (conversionTime, rgbPlanar) = timed {
        VideoConversions.YUVtoRGB(width.value, height.value, y, u, v, yStride, uStride, vStride)
      }

      val averageR = average(rgbPlanar._1)
      val averageG = average(rgbPlanar._2)
      val averageB = average(rgbPlanar._3)

      val receivedColor = new Color(averageR, averageG, averageB)

      val state = state0.modify(s => s.copy(
        t = s.t + 1,
        received = receivedColor :: s.received
      ))

      assert(state.get.t <= video.length)

      val minReceived = (video.length * minCompletionRatio).toInt
      val receivedMessage = s"Received frame ${state.get.t} of (minimum) $minReceived (YUVtoRGB: $conversionTime ms)"
      if (state.get.t >= minReceived) {
        val assertionTime: Int = timed {
          val received = state.get.received.distinct
          val expected = VideoGenerators.Colors.values

          // All received in expected.
          assert(received.forall { c => expected.exists(approximatelyEqual(c)) })

          if (state.get.t >= video.length) {
            // All expected in received. Only checked if every sent frame was received.
            debug(state, s"Received all ${video.length} frames")
            assert(expected.forall { c => received.exists(approximatelyEqual(c)) }): Unit
          }
        }

        debug(state, receivedMessage + s" (assertion time: $assertionTime ms)")

        state.finish
      } else {
        debug(state, receivedMessage)

        state
      }
    }

  }

}
