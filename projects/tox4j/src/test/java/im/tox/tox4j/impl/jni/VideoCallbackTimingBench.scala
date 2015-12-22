package im.tox.tox4j.impl.jni

import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.av.callbacks.ToxAvEventAdapter
import im.tox.tox4j.av.callbacks.video.VideoGenerators
import im.tox.tox4j.bench.TimingReport
import im.tox.tox4j.bench.ToxBenchBase._
import im.tox.tox4j.core.data.ToxFriendNumber

final class VideoCallbackTimingBench extends TimingReport {

  val VideoLength = 30

  val friendNumber = ToxFriendNumber.fromInt(1).get

  val width = VideoGenerators.DefaultWidth
  val height = VideoGenerators.DefaultHeight
  val yStride = width.value + 80
  val uStride = yStride / 2
  val vStride = yStride / 2

  val y = Array.ofDim[Byte](yStride * height.value)
  val u = Array.ofDim[Byte](uStride * height.value / 2)
  val v = Array.ofDim[Byte](uStride * height.value / 2)

  object EventListener extends ToxAvEventAdapter[Unit] with Serializable

  timing of s"VideoReceiveFrame ${width}x$height" in {

    val invokeAll: (((Int, ToxAv)) => Unit) = {
      case (frames, av) =>
        // Invoke all the events beforehand.
        (0 until frames) foreach { _ =>
          av.invokeVideoReceiveFrame(friendNumber, width, height, y, u, v, yStride, uStride, vStride)
        }
    }

    measure method "ToxAv.iterate" in {
      usingToxAv(range("frames")(VideoLength)) setUp invokeAll in {
        case (frames, av) =>
          // Then fetch and process them in the timed section.
          av.iterate(EventListener)(())
      }
    }

    measure method "ToxAvJni.toxAvIterate" in {
      usingToxAv(range("frames")(VideoLength)) setUp invokeAll in {
        case (frames, av: ToxAvImpl) =>
          // Then fetch them in the timed section.
          ToxAvJni.toxavIterate(av.instanceNumber)
      }
    }

  }

}
