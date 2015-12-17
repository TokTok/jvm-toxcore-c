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

  object EventListener extends ToxAvEventAdapter[Int] with Serializable {
    override def audioReceiveFrame(
      friendNumber: ToxFriendNumber,
      pcm: Array[Short],
      channels: AudioChannels,
      samplingRate: SamplingRate
    )(count: Int): Int = {
      assert(friendNumber == AvCallbackTimingBench.this.friendNumber)
      assert(pcm.length == AvCallbackTimingBench.this.pcm.length)
      assert(channels == AvCallbackTimingBench.this.channels)
      assert(samplingRate == AvCallbackTimingBench.this.samplingRate)
      count + 1
    }
  }

  def getInstanceNumber(av: ToxAv): Int = {
    av.asInstanceOf[ToxAvImpl].instanceNumber
  }

  timing of "AudioReceiveFrame" in {

    val invokeAll: (((Int, ToxAv)) => Unit) = {
      case (frames, toxAv) =>
        // Invoke all the events beforehand.
        (0 until frames) foreach { _ =>
          toxAv.invokeAudioReceiveFrame(friendNumber, pcm, channels, samplingRate)
        }
    }

    val invokeAllJni: (((Int, Int)) => Unit) = {
      case (frames, instanceNumber) =>
        // Invoke all the events beforehand.
        (0 until frames) foreach { _ =>
          ToxAvJni.invokeAudioReceiveFrame(instanceNumber, friendNumber.value, pcm, channels.value, samplingRate.value)
        }
    }

    measure method "ToxAv.iterate" in {
      usingToxAv(range("frames")(1000)) setUp invokeAll in {
        case (frames, toxAv) =>
          // Then fetch and process them in the timed section.
          val count = toxAv.iterate(EventListener)(0)
          assert(count == frames)
      }
    }

    measure method "ToxAvJni.toxAvIterate" in {
      using(range("frames")(1000), toxAvInstance.map(getInstanceNumber)) setUp invokeAllJni in {
        case (frames, instanceNumber) =>
          // Then fetch them in the timed section.
          ToxAvJni.toxavIterate(instanceNumber)
      }
    }

  }

}
