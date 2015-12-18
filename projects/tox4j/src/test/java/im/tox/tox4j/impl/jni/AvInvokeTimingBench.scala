package im.tox.tox4j.impl.jni

import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.av.data.{AudioChannels, AudioLength, SampleCount, SamplingRate}
import im.tox.tox4j.bench.TimingReport
import im.tox.tox4j.bench.ToxBenchBase._
import im.tox.tox4j.core.data.ToxFriendNumber

final class AvInvokeTimingBench extends TimingReport {

  val audioLength = AudioLength.Length60
  val channels = AudioChannels.Mono
  val samplingRate = SamplingRate.Rate48k

  val pcm = Array.ofDim[Short](
    SampleCount(audioLength, samplingRate).value * channels.value
  )

  val friendNumber = ToxFriendNumber.fromInt(1).get

  timing.of[ToxAv] {

    measure method "invokeAudioReceiveFrame" in {
      usingToxAv(range("frames")(10000)) tearDown {
        case (frames, av: ToxAvImpl) =>
          ToxAvJni.toxavIterate(av.instanceNumber, Array.empty)
      } in {
        case (frames, av) =>
          (0 until frames) foreach { _ =>
            av.invokeAudioReceiveFrame(friendNumber, pcm, channels, samplingRate)
          }
      }
    }

  }

}
