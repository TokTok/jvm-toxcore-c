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

  val ItCrowd = generator(96000 + 4000 * 8) { (t0, length) =>
    // Period
    val t = t0 % length

    val a = 1 / (128000 - t)
    val b = if (t > 96000) {
      t % 4000 * ("'&(&*$,*".charAt(t % 96000 / 4000) - 32)
    } else {
      {
        t % 2000 * {
          "$$$&%%%''''%%%'&".charAt(t % 32000 / 2000) - 32 -
            int(t > 28000 && t < 32000) * 2
        }
      } / {
        int(t % 8000 < 4000) + 1
      }
    }
    ((a | b) << 8).toShort / 4
  }

  val MortalKombat = generator { t =>
    val a = {
      2 * t % 4000 * {
        "!!#!$!%$".charAt(t % 16000 / 2000) - 32 + int(t % 64000 > 32000) * 7
      }
    } * {
      int(t % 32000 > 16000) + 1
    }
    val b = int(t % 128000 > 64000) * 2 * t * {
      "%%%'%+''%%%$%+))%%%%'+'%$%%%$%%%$%%".charAt(t % 70000 / 2000) - 36
    }
    ((a | b) << 8).toShort / 4
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

  private def play(t: Int, tones: Seq[Double], tempo: Int): Double = {
    val index = t % (tones.length * tempo) / tempo
    val previous =
      if (index == 0) {
        tones.length - 1
      } else {
        index - 1
      }
    tones(index) * int(t % tempo > 300 || tones(previous) == tones(index))
  }

  private def play(t: Int, tones: String, tempo: Int): Double = {
    play(t, tones.map(_.toDouble), tempo)
  }

  // https://www.youtube.com/watch?v=S7dg0X1LskI
  private val SongOfStorms = generator(1800 * 4 * 16) { (t, _) =>
    val melody = play(t, Seq[Double](
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
    ), 1800)
    val bass = play(t, Seq[Double](
      73.41, 82.4, 87.3, 82.4,
      116.54, 87.3, 116.54, 110,

      73.41, 82.4, 87.3, 82.4,
      116.54, 110, 73.41, 73.41
    ), 7200)
    val organ = play(t, Seq[Double](
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
    ), 900)
    val percussions = play(t, Seq[Double](
      0, 1, 0, 1,
      0, 0, 0, 0,
      1, 1, 1, 1,
      0, 0, 0, 0
    ), 900) * Math.random() * int(t % 900 > 700)

    val osc = new Oscillator(8000)
    Seq(
      osc.sine(t, melody, 0.35),
      osc.sawtooth(t, bass, 0.15),
      osc.sawtooth(t, organ, 0.15),
      osc.sawtooth(t, percussions, 0.15),
      0.0
    ).map(_ * Short.MaxValue).sum.toShort
  }

  // Selected audio generator for tests.
  val Selected = SongOfStorms

}
