package im.tox.tox4j.impl.jni

import java.nio.ByteBuffer

import com.google.protobuf.ByteString
import im.tox.tox4j.av.callbacks.ToxAvEventAdapter
import im.tox.tox4j.av.data._
import im.tox.tox4j.av.proto.{AvEvents, AudioReceiveFrame}
import im.tox.tox4j.bench.TimingReport
import im.tox.tox4j.bench.ToxBenchBase._

final class AudioReceiveFrameTimingBench extends TimingReport {

  timing.of[AudioReceiveFrame] {

    val audioLength = AudioLength.Length60
    val channels = AudioChannels.Mono
    val samplingRate = SamplingRate.Rate48k

    val frame = AudioReceiveFrame(0, ByteString.copyFrom(ByteBuffer.wrap(Array.ofDim[Byte](
      SampleCount(audioLength, samplingRate).value * channels.value * 2
    ))))

    val frames = range("frames")(10000).map { count =>
      AvEvents(audioReceiveFrame = (0 until count) map (_ => frame)).toByteArray
    }

    val handler = new ToxAvEventAdapter[Unit] with Serializable

    performance of "60ms per frame at 48k" in {
      using(frames) in { eventData =>
        ToxAvEventDispatch.dispatch(handler, eventData)(())
      }
    }

  }

}
