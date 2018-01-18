package im.tox.tox4j.impl.jni

import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.av.callbacks.video.VideoGenerators
import im.tox.tox4j.bench.TimingReport
import im.tox.tox4j.bench.ToxBenchBase._
import im.tox.tox4j.core.data.ToxFriendNumber

final class VideoInvokeTimingBench extends TimingReport {

  private val friendNumber = ToxFriendNumber.fromInt(1).get

  private val width = VideoGenerators.DefaultWidth
  private val height = VideoGenerators.DefaultHeight
  private val yStride = width.value + 80
  private val uStride = yStride / 2
  private val vStride = yStride / 2

  private val y = Array.ofDim[Byte](yStride * height.value)
  private val u = Array.ofDim[Byte](uStride * height.value / 2)
  private val v = Array.ofDim[Byte](uStride * height.value / 2)

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
