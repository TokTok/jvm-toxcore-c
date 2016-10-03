package im.tox.tox4j.av.callbacks.audio

import java.util

import com.typesafe.scalalogging.Logger
import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.av.data._
import im.tox.tox4j.av.enums.ToxavFriendCallState
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.testing.ToxExceptionChecks
import im.tox.tox4j.testing.autotest.AutoTestSuite
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.language.postfixOps

@SuppressWarnings(Array("org.wartremover.warts.Equals"))
final class AudioReceiveFrameCallbackTest extends AutoTestSuite with ToxExceptionChecks {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  override def maxParticipantCount: Int = 2

  final case class S(t: Int = 0, frequencies: List[Double] = Nil)

  object Handler extends EventListener(S()) {

    /**
     * How much of the sent data must be received for the test to pass.
     */
    private val minCompletionRatio = 0.8

    private val audioFrequency = 220
    private val audio = AudioGenerators.Sawtooth(audioFrequency)

    private val bitRate = BitRate.fromInt(320).get
    private val audioPerFrame = AudioLength.Length60
    private val samplingRate = SamplingRate.Rate8k
    private val frameSize = SampleCount(audioPerFrame, samplingRate).value
    private val windowSize = audio.length(samplingRate) / frameSize / 2
    private val framesPerIteration = 2

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
          debug(state, s"Ringing ${state.id(friendNumber)}")
          av.call(friendNumber, bitRate, BitRate.Disabled)
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
      val state = state0.modify(s => s.copy(t = s.t + frameSize * framesPerIteration))

      for (t <- state0.get.t until state.get.t by frameSize) {
        val pcm = audio.nextFrame16(audioPerFrame, samplingRate, t)
        av.audioSendFrame(
          friendNumber,
          pcm,
          SampleCount(audioPerFrame, samplingRate),
          AudioChannels.Mono,
          samplingRate
        )
      }

      if (state.get.t >= audio.length(samplingRate)) {
        state.finish
      } else {
        state.addTask(sendFrame(friendNumber))
      }
    }

    override def callState(friendNumber: ToxFriendNumber, callState: util.EnumSet[ToxavFriendCallState])(state: State): State = {
      debug(state, s"Call with ${state.id(friendNumber)} is now $callState")
      assert(callState == util.EnumSet.of(
        ToxavFriendCallState.ACCEPTING_A,
        ToxavFriendCallState.ACCEPTING_V
      ))
      state.addTask(sendFrame(friendNumber))
    }

    override def bitRateStatus(friendNumber: ToxFriendNumber, audioBitRate: BitRate, videoBitRate: BitRate)(state: State): State = {
      debug(state, s"Bit rate in call with ${state.id(friendNumber)} should change to $audioBitRate for audio and $videoBitRate for video")
      state
    }

    override def audioReceiveFrame(
      friendNumber: ToxFriendNumber,
      pcm: Array[Short],
      channels: AudioChannels,
      samplingRate: SamplingRate
    )(state0: State): State = {
      val binarised = pcm.map { n =>
        if (n > 0) {
          Short.MaxValue / 2
        } else {
          Short.MinValue / 2
        }
      }

      val derivative = (binarised, binarised.tail).zipped.map {
        (n0, n1) => (n1 - n0).toShort
      }

      val vibrations = derivative.count(_ != 0)

      val frequency = vibrations * ((1 second) / audioPerFrame.value) / 2

      val state = state0.modify(s => s.copy(
        t = s.t + pcm.length,
        frequencies = (frequency :: s.frequencies).take(windowSize)
      ))

      val averageFrequency = state.get.frequencies.sum / state.get.frequencies.length
      debug(state, s"Received audio frame: ${state.get.t} / ${audio.length(samplingRate)} (f~=$averageFrequency (last ${state.get.frequencies.length} frames))")
      assert(channels == AudioChannels.Mono)
      assert(samplingRate == this.samplingRate)

      // Require at least half the frames to arrive.
      if (state.get.t >= audio.length(samplingRate) * minCompletionRatio) {
        assert(Math.round(averageFrequency) == audioFrequency)
        state.finish
      } else {
        state
      }
    }

  }

}
