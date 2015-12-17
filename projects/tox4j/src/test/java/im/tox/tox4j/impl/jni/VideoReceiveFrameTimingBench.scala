package im.tox.tox4j.impl.jni

import com.google.protobuf.ByteString
import im.tox.tox4j.av.callbacks.ToxAvEventAdapter
import im.tox.tox4j.av.data._
import im.tox.tox4j.av.proto.{AvEvents, VideoReceiveFrame}
import im.tox.tox4j.bench.TimingReport
import im.tox.tox4j.bench.ToxBenchBase._

final class VideoReceiveFrameTimingBench extends TimingReport {

  timing.of[VideoReceiveFrame] {

    val width = 100
    val height = 100
    val y = ByteString.copyFrom(Array.ofDim[Byte](width * height))
    val uv = ByteString.copyFrom(Array.ofDim[Byte](width * height / 4))
    val frame = VideoReceiveFrame(0, width, height, y, uv, uv, width, width, width)

    val frames = range("frames")(10000).map { count =>
      AvEvents(videoReceiveFrame = (0 until count) map (_ => frame)).toByteArray
    }

    val nonCachingHandler = new ToxAvEventAdapter[Unit] with Serializable

    val cachingHandler = new ToxAvEventAdapter[Unit] with Serializable {
      val cache = Some((
        Array.ofDim[Byte](width * height),
        Array.ofDim[Byte](width * height / 4),
        Array.ofDim[Byte](width * height / 4)
      ))

      override def videoFrameCachedYUV(width: Width, height: Height): Option[(Array[Byte], Array[Byte], Array[Byte])] = {
        cache
      }
    }

    performance of s"${width}x$height" in {
      using(frames) in { eventData =>
        ToxAvEventDispatch.dispatch(nonCachingHandler, eventData)(())
      }
    }

    performance of s"${width}x$height (cached)" in {
      using(frames) in { eventData =>
        ToxAvEventDispatch.dispatch(cachingHandler, eventData)(())
      }
    }

  }

}
