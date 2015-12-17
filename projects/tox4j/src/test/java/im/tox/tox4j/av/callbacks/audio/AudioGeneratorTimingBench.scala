package im.tox.tox4j.av.callbacks.audio

import im.tox.tox4j.av.data.{AudioLength, SamplingRate}
import im.tox.tox4j.bench.ToxBenchBase._
import im.tox.tox4j.bench.{Confidence, TimingReport}

final class AudioGeneratorTimingBench extends TimingReport {

  protected override def confidence = Confidence.normal

  val audioLength = AudioLength.Length60
  val audioSamplingRate = SamplingRate.Rate8k

  val frames1k = range("frames")(1000)

  def performanceTest(name: String, generator: AudioGenerator): Unit = {
    performance of name in {
      using(frames1k) in { frames =>
        var t = 0
        while (t < frames) {
          generator.nextFrame16(audioLength, audioSamplingRate, t)
          t += 1
        }
      }
    }
  }

  timing.of[AudioGenerator] {

    performanceTest("ItCrowd", AudioGenerators.ItCrowd)
    performanceTest("MortalKombat", AudioGenerators.MortalKombat)
    performanceTest("Sine1", AudioGenerators.Sine1)
    performanceTest("Sine2", AudioGenerators.Sine2)
    performanceTest("Sine3", AudioGenerators.Sine3)
    performanceTest("SongOfStorms", AudioGenerators.SongOfStorms)

  }

}
