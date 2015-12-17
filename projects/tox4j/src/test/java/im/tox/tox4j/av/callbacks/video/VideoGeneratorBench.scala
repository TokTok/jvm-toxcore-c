package im.tox.tox4j.av.callbacks.video

import im.tox.tox4j.av.data.{Height, Width}
import im.tox.tox4j.bench.{Confidence, TimingReport}
import im.tox.tox4j.bench.ToxBenchBase._

final class VideoGeneratorBench extends TimingReport {

  protected override def confidence = Confidence.normal

  val frames100k = range("frames")(500)

  timing of classOf[VideoGenerator] in {

    performance of "Xor5" in {

      using(frames100k) in { frames =>
        val generator = VideoGenerators
          .Xor5(VideoGenerators.DefaultWidth, VideoGenerators.DefaultHeight)

        var i = 0
        while (i < frames) {
          generator.yuv(i)
          i += 1
        }
      }
    }

    performance of "Xor5.resize(401, 401)" in {

      using(frames100k) in { frames =>
        val generator = VideoGenerators
          .Xor5(VideoGenerators.DefaultWidth, VideoGenerators.DefaultHeight)
          .resize(Width(401), Height(401))

        var i = 0
        while (i < frames) {
          generator.yuv(i)
          i += 1
        }
      }
    }

    /*
    performance of "Zero" in {
      using(frames100k) in { frames =>
        (0 until frames) foreach VideoGenerators.ByteMax.yuv
      }
    }
    */

  }

}
