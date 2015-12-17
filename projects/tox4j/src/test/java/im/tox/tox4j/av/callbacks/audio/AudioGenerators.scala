package im.tox.tox4j.av.callbacks.audio

import im.tox.tox4j.av.callbacks.audio.AudioGenerator._
import im.tox.tox4j.av.data.SamplingRate

object AudioGenerators {

  case object ItCrowd extends AudioGenerator {

    def length(samplingRate: SamplingRate): Int = (samplingRate.value * 12) + (samplingRate.value / 2) * 8

    def sample(samplingRate: SamplingRate, t0: Int): Int = {
      // Period
      val t = t0 % length(samplingRate)

      val rate = samplingRate.value

      val a = 1 / ((rate * 16) - t)
      val b =
        if (t > (rate * 12)) {
          t % (rate / 2) * ("'&(&*$,*".charAt(t % (rate * 12) / (rate / 2)) - 32)
        } else {
          {
            t % (rate / 4) * {
              "$$$&%%%''''%%%'&".charAt(t % (rate * 4) / (rate / 4)) - 32 -
                int(t > (rate * 3 + rate / 2) && t < (rate * 4)) * 2
            }
          } / {
            int(t % rate < (rate / 2)) + 1
          }
        }
      ((a | b) / (rate / 8000) << 8).toShort / 5
    }

  }

  case object MortalKombat extends AudioGenerator {
    def length(samplingRate: SamplingRate): Int = samplingRate.value * 16

    def sample(samplingRate: SamplingRate, t: Int): Int = {
      val rate = samplingRate.value

      val a = {
        2 * t % (rate / 2) * {
          "!!#!$!%$".charAt(t % (rate * 2) / (rate / 4)) - 32 +
            int(t % (rate * 8) > (rate * 4)) * 7
        }
      } * {
        int(t % (rate * 4) > (rate * 2)) + 1
      }
      val b = int(t % (rate * 16) > (rate * 8)) * 2 * t * {
        "%%%'%+''%%%$%+))%%%%'+'%$%%%$%%%$%%".charAt(t % (rate * 8 + (rate - rate / 4)) / (rate / 4)) - 36
      }
      ((a | b) / (rate / 8000) << 8).toShort / 5
    }
  }

  case object Sine1 extends AudioGenerator {
    def length(samplingRate: SamplingRate): Int = samplingRate.value * 16
    def sample(samplingRate: SamplingRate, t: Int): Int = (Math.sin(t / (10000d / t)) * 128).toShort << 6
  }

  case object Sine2 extends AudioGenerator {
    def length(samplingRate: SamplingRate): Int = samplingRate.value * 16
    def sample(samplingRate: SamplingRate, t0: Int): Int = {
      val t =
        if (t0 % 2 == 0) {
          1
        } else {
          t0
        }
      (Math.sin(t / (10000d / t)) * 128).toShort << 6
    }
  }

  case object Sine3 extends AudioGenerator {
    def length(samplingRate: SamplingRate): Int = samplingRate.value * 16
    def sample(samplingRate: SamplingRate, t: Int): Int = {
      ((t & 0xF) * ((0 - t) & 0xF) * (((t & 0x10) >> 3) - 1) * 0x80 / 0x41 + 0x80) << 7
    }
  }

  // https://www.youtube.com/watch?v=S7dg0X1LskI
  case object SongOfStorms extends AudioGenerator {

    def length(samplingRate: SamplingRate): Int = samplingRate.value * 16

    private val melody = (4, Array[Double](
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
    ))

    private val bass = (1, Array[Double](
      73.41, 82.4, 87.3, 82.4,
      116.54, 87.3, 116.54, 110,

      73.41, 82.4, 87.3, 82.4,
      116.54, 110, 73.41, 73.41
    ))

    private val organ = (8, Array[Double](
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
    ))

    private val percussions = (8, Array[Double](
      0, 1, 0, 1,
      0, 0, 0, 0,
      1, 1, 1, 1,
      0, 0, 0, 0
    ))

    private def playInstrument(samplingRate: SamplingRate, t: Int, input: (Int, Array[Double])): Double = {
      val (tempo, tones) = input
      playTones(t, tones, samplingRate, tempo)
    }

    private def playPercussions(samplingRate: SamplingRate, t: Int, input: (Int, Array[Double])): Double = {
      val (tempo, tones) = input
      playWhiteNoise(t, tones, samplingRate, tempo, 1.0 / 40)
    }

    def sample(samplingRate: SamplingRate, t: Int): Int = {
      val double = (0
        + Oscillator.Sine.osc(samplingRate, t, playInstrument(samplingRate, t, melody), 0.35)
        + Oscillator.Sawtooth.osc(samplingRate, t, playInstrument(samplingRate, t, bass), 0.1)
        + Oscillator.Sawtooth.osc(samplingRate, t, playInstrument(samplingRate, t, organ), 0.05)
        + Oscillator.Sawtooth.osc(samplingRate, t, playPercussions(samplingRate, t, percussions), 0.1))
      (double * Short.MaxValue).toShort
    }

  }

  // Selected audio generator for tests.
  def default: AudioGenerator = SongOfStorms

}
