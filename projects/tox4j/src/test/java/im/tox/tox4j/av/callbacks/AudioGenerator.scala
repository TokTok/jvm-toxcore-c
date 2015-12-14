package im.tox.tox4j.av.callbacks

final case class AudioGenerator(
    sample: Int => Int,
    length: Int = 128000
) {

  def nextFrame16(t: Int, frameSize: Int): Array[Short] = {
    val frame = Array.ofDim[Short](frameSize)
    for (i <- frame.indices) {
      frame(i) = sample(t + i).toShort
    }
    frame
  }

}

object AudioGenerator {

  private def generator(length: Int)(sample: (Int, Int) => Int): AudioGenerator = {
    AudioGenerator(t => sample(t, length), length)
  }

  private def generator(sample: Int => Int): AudioGenerator = {
    AudioGenerator(sample)
  }

  private def int(boolean: Boolean): Int = if (boolean) 1 else 0

  def itCrowd(samplingRate: Int): AudioGenerator = generator((samplingRate * 12) + (samplingRate / 2) * 8) { (t0, length) =>
    // Period
    val t = t0 % length

    val a = 1 / ((samplingRate * 16) - t)
    val b =
      if (t > (samplingRate * 12)) {
        t % (samplingRate / 2) * ("'&(&*$,*".charAt(t % (samplingRate * 12) / (samplingRate / 2)) - 32)
      } else {
        {
          t % (samplingRate / 4) * {
            "$$$&%%%''''%%%'&".charAt(t % (samplingRate * 4) / (samplingRate / 4)) - 32 -
              int(t > (samplingRate * 3 + samplingRate / 2) && t < (samplingRate * 4)) * 2
          }
        } / {
          int(t % samplingRate < (samplingRate / 2)) + 1
        }
      }
    ((a | b) / (samplingRate / 8000) << 8).toShort / 5
  }

  def mortalKombat(samplingRate: Int): AudioGenerator = generator(samplingRate * 16) { (t, _) =>
    val a = {
      2 * t % (samplingRate / 2) * {
        "!!#!$!%$".charAt(t % (samplingRate * 2) / (samplingRate / 4)) - 32 +
          int(t % (samplingRate * 8) > (samplingRate * 4)) * 7
      }
    } * {
      int(t % (samplingRate * 4) > (samplingRate * 2)) + 1
    }
    val b = int(t % (samplingRate * 16) > (samplingRate * 8)) * 2 * t * {
      "%%%'%+''%%%$%+))%%%%'+'%$%%%$%%%$%%".charAt(t % (samplingRate * 8 + (samplingRate - samplingRate / 4)) / (samplingRate / 4)) - 36
    }
    ((a | b) / (samplingRate / 8000) << 8).toShort / 5
  }

  val Sine = generator { t =>
    (Math.sin(t / (10000d / t)) * 128).toShort << 6
  }

  val Sine2 = generator { t0 =>
    val t =
      if (t0 % 2 == 0) {
        1
      } else {
        t0
      }
    (Math.sin(t / (10000d / t)) * 128).toShort << 6
  }

  val Sine3 = generator { t =>
    ((t & 0xF) * ((0 - t) & 0xF) * (((t & 0x10) >> 3) - 1) * 0x80 / 0x41 + 0x80) << 7
  }

  private final class Oscillator(val sampleRate: Int) extends AnyVal {
    private def osc(shape: Double => Double)(t: Int, frequency: Double, volume: Double): Double = {
      val samples = sampleRate / frequency
      shape(t % samples / samples) * volume
    }

    type Osc = (Int, Double, Double) => Double

    def sine: Osc = osc(x => Math.sin(2.0 * Math.PI * x))
    def sawtooth: Osc = osc(x => 2.0 * (x - Math.floor(x + 0.5)))
  }

  private def play(t: Int, tones: Seq[Double], samplingRate: Int, tempo: Int): Double = {
    // The duration of the tone in number of samples.
    val duration = samplingRate / tempo
    // A short pause between each tone played.
    val pause = samplingRate / 40

    val index = t % (tones.length * duration) / duration
    val previous =
      if (index == 0) {
        tones.length - 1
      } else {
        index - 1
      }
    tones(index) * int(t % duration > pause || tones(previous) == tones(index))
  }

  private def play(t: Int, tones: String, samplingRate: Int, tempo: Int): Double = {
    play(t, tones.map(_.toDouble), samplingRate, tempo)
  }

  private def playNoise(t: Int, tones: Seq[Int], samplingRate: Int, tempo: Int, duration: Double): Double = {
    play(t, tones.map(_.toDouble), samplingRate, tempo) * Math.random() * {
      if (t % (samplingRate / 8) > (samplingRate / 8 - (samplingRate * duration))) {
        1.0
      } else {
        0.0
      }
    }
  }

  private final case class SongOfStorms(samplingRate: Int) extends AnyVal {

    private def melody(t: Int) = play(t, Seq[Double](
      146.83, 174.61, 293.66, 293.66,
      146.83, 174.61, 293.66, 293.66,
      329.62, 349.22, 329.62, 349.22, 329.62, 261.62, 220, 220,

      220, 220, 146.83, 146.83, 174.61, 195.99, 220, 220,
      220, 220, 146.83, 146.83, 174.61, 195.99, 164.81, 164.81,

      146.83, 174.61, 293.66, 293.66,
      146.83, 174.61, 293.66, 293.66,
      329.62, 349.22, 329.62, 349.22, 329.62, 261.62, 220, 220,

      220, 220, 146.83, 146.83, 174.61, 195.99, 220, 220,
      220, 220, 146.83, 146.83, 146.83, 146.83, 146.83, 146.83
    ), samplingRate, 4)

    private def bass(t: Int) = play(t, Seq[Double](
      73.41, 82.4, 87.3, 82.4,
      116.54, 87.3, 116.54, 110,

      73.41, 82.4, 87.3, 82.4,
      116.54, 110, 73.41, 73.41
    ), samplingRate, 1)

    private def organ(t: Int) = play(t, Seq[Double](
      0, 220, 0, 220,
      0, 0, 0, 146.83,
      246.94, 246.94, 246.94, 246.94,
      0, 0, 0, 0,

      0, 261.62, 0, 261.62,
      0, 0, 0, 146.83,
      246.94, 246.94, 246.94, 246.94,
      0, 0, 0, 0,

      0, 220, 0, 220,
      0, 0, 0, 220,
      220, 220, 220, 220,
      0, 0, 0, 0,

      0, 220, 0, 220,
      0, 0, 0, 220,
      220, 220, 220, 220,
      0, 0, 0, 0
    ), samplingRate, 8)

    private def percussions(t: Int) = playNoise(t, Seq(
      0, 1, 0, 1,
      0, 0, 0, 0,
      1, 1, 1, 1,
      0, 0, 0, 0
    ), samplingRate, 8, 1.0 / 40)

    def sample(t: Int): Short = {
      val osc = new Oscillator(samplingRate)
      Seq(
        osc.sine(t, melody(t), 0.35),
        osc.sawtooth(t, bass(t), 0.1),
        osc.sawtooth(t, organ(t), 0.05),
        osc.sawtooth(t, percussions(t), 0.1),
        0.0
      ).map(_ * Short.MaxValue).sum.toShort
    }

  }

  // https://www.youtube.com/watch?v=S7dg0X1LskI
  private def songOfStorms(samplingRate: Int) = generator(samplingRate * 16) { (t, _) =>
    SongOfStorms(samplingRate).sample(t)
  }

  // Selected audio generator for tests.
  def apply(samplingRate: Int): AudioGenerator = songOfStorms(samplingRate)

}
