package im.tox.tox4j.impl.jni

import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.av.callbacks.ToxAvEventAdapter
import im.tox.tox4j.av.data.{AudioChannels, AudioLength, SampleCount, SamplingRate}
import im.tox.tox4j.bench.TimingReport
import im.tox.tox4j.bench.ToxBenchBase._
import im.tox.tox4j.core.data.ToxFriendNumber

final class AvCallbackTimingBench extends TimingReport {

  val audioLength = AudioLength.Length60
  val channels = AudioChannels.Mono
  val samplingRate = SamplingRate.Rate48k

  val pcm = Array.ofDim[Short](
    SampleCount(audioLength, samplingRate).value * channels.value
  )

  val friendNumber = ToxFriendNumber.fromInt(1).get

  object EventListener extends ToxAvEventAdapter[Unit] with Serializable

  timing of s"AudioReceiveFrame (${pcm.length} samples)" in {

    val invokeAll: (((Int, ToxAv)) => Unit) = {
      case (frames, toxAv) =>
        // Invoke all the events beforehand.
        (0 until frames) foreach { _ =>
          toxAv.invokeAudioReceiveFrame(friendNumber, pcm, channels, samplingRate)
        }
    }

    measure method "ToxAv.iterate" in {
      usingToxAv(range("frames")(10000)) setUp invokeAll in {
        case (frames, av) =>
          // Then fetch and process them in the timed section.
          av.iterate(EventListener)(())
      }
    }

    measure method "ToxAvJni.toxAvIterate" in {
      usingToxAv(range("frames")(10000)) setUp invokeAll in {
        case (frames, av: ToxAvImpl) =>
          // Then fetch them in the timed section.
          ToxAvJni.toxavIterate(av.instanceNumber, Array.empty)
      }
    }

    measure method "ToxAvJni.toxAvIterate (cached)" in {
      var cache = Array.empty[Byte]

      usingToxAv(range("frames")(10000)) setUp invokeAll in {
        case (frames, av: ToxAvImpl) =>
          // Then fetch them in the timed section.
          cache = ToxAvJni.toxavIterate(av.instanceNumber, cache)
      }
    }

  }

}
