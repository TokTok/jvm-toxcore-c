package im.tox.tox4j.impl.jni

import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.av.callbacks.video.VideoGenerators
import im.tox.tox4j.bench.TimingReport
import im.tox.tox4j.bench.ToxBenchBase._
import im.tox.tox4j.core.data.ToxFriendNumber

final class VideoInvokeTimingBench extends TimingReport {

  val friendNumber = ToxFriendNumber.fromInt(1).get

  val width = VideoGenerators.DefaultWidth
  val height = VideoGenerators.DefaultHeight
  val yStride = width.value + 80
  val uStride = yStride / 2
  val vStride = yStride / 2

  val y = Array.ofDim[Byte](yStride * height.value)
  val u = Array.ofDim[Byte](uStride * height.value / 2)
  val v = Array.ofDim[Byte](uStride * height.value / 2)

  timing.of[ToxAv] {

    measure method "invokeVideoReceiveFrame" in {
      usingToxAv(range("frames")(100)) tearDown {
        case (frames, av: ToxAvImpl) =>
          ToxAvJni.toxavIterate(av.instanceNumber)
      } in {
        case (frames, av) =>
          (0 until frames) foreach { _ =>
            av.invokeVideoReceiveFrame(friendNumber, width, height, y, u, v, yStride, uStride, vStride)
          }
      }
    }

  }

}
