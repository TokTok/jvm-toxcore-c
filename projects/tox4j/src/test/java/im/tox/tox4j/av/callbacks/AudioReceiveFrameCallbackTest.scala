package im.tox.tox4j.av.callbacks

import java.util

import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.av.data._
import im.tox.tox4j.av.enums.ToxavFriendCallState
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.testing.ToxExceptionChecks
import im.tox.tox4j.testing.autotest.AutoTestSuite
import jline.TerminalFactory

final class AudioReceiveFrameCallbackTest extends AutoTestSuite with ToxExceptionChecks {

  private def audio = AudioGenerator.Selected

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
          av.call(friendNumber, BitRate.fromInt(320).get, BitRate.Disabled)
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
      val state = state0.modify(_ + 60 * 8)

      val pcm = audio.nextFrame16(state0.get, 60 * 8)
      av.audioSendFrame(
        friendNumber,
        pcm,
        SampleCount(AudioLength.Length60, SamplingRate.Rate8k),
        AudioChannels.Mono,
        SamplingRate.Rate8k
      )

      if (state.get >= audio.length) {
        state.finish
      } else {
        state.addTask(sendFrame(friendNumber))
      }
    }

    override def callState(friendNumber: Int, callState: util.Collection[ToxavFriendCallState])(state: State): State = {
      debug(state, s"Call with ${state.id(friendNumber)} is now $callState")
      state.addTask(sendFrame(friendNumber))
    }

    override def audioReceiveFrame(
      friendNumber: Int,
      pcm: Array[Short],
      channels: AudioChannels,
      samplingRate: SamplingRate
    )(state0: State): State = {
      val state = state0.modify(_ + pcm.length)
      val expectedPcm = audio.nextFrame16(state0.get, 60 * 8)

      debug(state, s"Received audio frame: ${state.get} / ${audio.length}")
      AudioPlayback.play(pcm)

      if (!sys.env.contains("TRAVIS")) {
        val width = TerminalFactory.get.getWidth
        System.out.print("\u001b[H\u001b[2J")
        System.out.println("Received:")
        System.out.println(AudioPlayback.showWave(pcm, width))
        System.out.println("Expected:")
        System.out.println(AudioPlayback.showWave(expectedPcm, width))
      }

      if (state.get >= audio.length) {
        state.finish
      } else {
        state
      }
    }

  }

}
