package im.tox.tox4j.av.callbacks

import java.util
import java.util.concurrent.ArrayBlockingQueue

import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.av.data._
import im.tox.tox4j.av.enums.ToxavFriendCallState
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.testing.ToxExceptionChecks
import im.tox.tox4j.testing.autotest.AutoTestSuite
import jline.TerminalFactory

import scalaz.concurrent.Future

final class AudioReceiveFrameCallbackTest extends AutoTestSuite with ToxExceptionChecks {

  private val audio = AudioGenerator.Selected
  private val displayWave = !sys.env.contains("TRAVIS")

  type S = Int

  object Handler extends EventListener(0) {

    val bitRate = BitRate.fromInt(8).get
    val audioLength = AudioLength.Length60
    val samplingRate = SamplingRate.Rate8k
    val frameSize = (audioLength.value.toMillis * samplingRate.value / 1000).toInt
    val framesPerIteration = 2

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
          av.call(friendNumber, bitRate, BitRate.Disabled)
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
      val state = state0.modify(_ + frameSize * framesPerIteration)

      for (t <- state0.get until state.get by frameSize) {
        val pcm = audio.nextFrame16(t, frameSize)
        av.audioSendFrame(
          friendNumber,
          pcm,
          SampleCount(audioLength, samplingRate),
          AudioChannels.Mono,
          samplingRate
        )
      }

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

    override def bitRateStatus(friendNumber: Int, audioBitRate: BitRate, videoBitRate: BitRate)(state: State): State = {
      debug(state, s"Bit rate in call with ${state.id(friendNumber)} should change to $audioBitRate for audio and $videoBitRate for video")
      state
    }

    def waitForPlayback(length: Int)(state: State): State = {
      if (!AudioPlayback.done(audio.length)) {
        state.addTask { (tox, av, state) =>
          waitForPlayback(length)(state)
        }
      } else {
        state.finish
      }
    }

    override def audioReceiveFrame(
      friendNumber: Int,
      pcm: Array[Short],
      channels: AudioChannels,
      samplingRate: SamplingRate
    )(state0: State): State = {
      val state = state0.modify(_ + pcm.length)

      debug(state, s"Received audio frame: ${state.get} / ${audio.length}")
      assert(channels == AudioChannels.Mono)
      assert(samplingRate == this.samplingRate)
      frameBuffer.add((state0.get, pcm))

      if (state.get >= audio.length) {
        waitForPlayback(audio.length)(state)
      } else {
        state
      }
    }

    private lazy val frameBuffer = {
      // Make the queue large enough to hold half the audio frames.
      val queue = new ArrayBlockingQueue[(Int, Array[Short])](audio.length / frameSize / 2)

      // Start a thread to consume the frames.
      Future {
        while (!AudioPlayback.done(audio.length)) {
          val (t, receivedPcm) = queue.take()
          val expectedPcm = audio.nextFrame16(t, frameSize)

          assert(receivedPcm.length == expectedPcm.length)

          if (displayWave) {
            val width = TerminalFactory.get.getWidth
            if (t == 0) {
              System.out.print("\u001b[2J")
            }
            System.out.print("\u001b[H")
            System.out.println("Received:")
            System.out.println(AudioPlayback.showWave(receivedPcm, width))
            System.out.println("Expected:")
            System.out.println(AudioPlayback.showWave(expectedPcm, width))
          }

          AudioPlayback.play(receivedPcm)
        }
      }.start

      queue
    }

  }

}
