package im.tox.tox4j.av.callbacks.audio

import java.util
import java.util.concurrent.ArrayBlockingQueue

import com.typesafe.scalalogging.Logger
import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.av.data._
import im.tox.tox4j.av.enums.ToxavFriendCallState
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.testing.ToxExceptionChecks
import im.tox.tox4j.testing.autotest.AutoTestSuite
import jline.TerminalFactory
import org.slf4j.LoggerFactory

import scala.annotation.tailrec
import scalaz.concurrent.Future

/**
 * This test name does not end in "Test" so it won't be run by the CI build. This is
 * because the test is flaky by nature: it expects every lossy audio packet to arrive.
 * We keep it around, because it's fun, and because it tests something that is very
 * hard to test otherwise: the received audio sounds good. This either requires a human
 * or an algorithm we're too lazy to implement.
 */
@SuppressWarnings(Array("org.wartremover.warts.Equals"))
final class AudioReceiveFrameCallbackShow extends AutoTestSuite with ToxExceptionChecks {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  override def maxParticipantCount: Int = 2

  type S = Int

  object Handler extends EventListener(0) {

    val bitRate = BitRate.fromInt(320).get
    val audioPerFrame = AudioLength.Length40
    val samplingRate = SamplingRate.Rate48k
    val frameSize = SampleCount(audioPerFrame, samplingRate).value
    val framesPerIteration = 2

    val audio = AudioGenerators.default
    val audioLength = audio.length(samplingRate)
    val playback = new AudioPlayback(samplingRate)
    val displayWave = !sys.env.contains("TRAVIS")

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
      val state = state0.modify(_ + frameSize * framesPerIteration)

      for (t <- state0.get until state.get by frameSize) {
        val pcm = audio.nextFrame16(audioPerFrame, samplingRate, t)
        av.audioSendFrame(
          friendNumber,
          pcm,
          SampleCount(audioPerFrame, samplingRate),
          AudioChannels.Mono,
          samplingRate
        )
      }

      if (state.get >= audioLength) {
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

    def waitForPlayback(length: Int)(state: State): State = {
      if (!playback.done(length)) {
        state.addTask { (tox, av, state) =>
          waitForPlayback(length)(state)
        }
      } else {
        state.finish
      }
    }

    override def audioReceiveFrame(
      friendNumber: ToxFriendNumber,
      pcm: Array[Short],
      channels: AudioChannels,
      samplingRate: SamplingRate
    )(state0: State): State = {
      val state = state0.modify(_ + pcm.length)

      debug(state, s"Received audio frame: ${state.get} / ${audio.length(samplingRate)}")
      assert(channels == AudioChannels.Mono)
      assert(samplingRate == this.samplingRate)
      frameBuffer.add(Some((state0.get, pcm)))

      if (state.get >= audio.length(samplingRate)) {
        frameBuffer.add(None)
        waitForPlayback(audio.length(samplingRate))(state)
      } else {
        state
      }
    }

    @tailrec
    private def playFrames(queue: ArrayBlockingQueue[Option[(Int, Array[Short])]]): Unit = {
      queue.take() match {
        case Some((t, receivedPcm)) =>
          val expectedPcm = audio.nextFrame16(audioPerFrame, samplingRate, t)

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

          playback.play(receivedPcm)

          playFrames(queue)

        case None =>
          logger.debug("Terminating audio playback thread")
      }
    }

    private lazy val frameBuffer = {
      // Make the queue large enough to hold half the audio frames.
      val queue = new ArrayBlockingQueue[Option[(Int, Array[Short])]](audioLength / frameSize / 2)

      // Start a thread to consume the frames.
      Future(playFrames(queue)).unsafeStart

      queue
    }

  }

}
