package im.tox.tox4j.av.callbacks.audio

import im.tox.tox4j.av.data.{SampleCount, AudioLength, SamplingRate}

abstract class AudioGenerator {

  def productPrefix: String

  def length(samplingRate: SamplingRate): Int
  def sample(samplingRate: SamplingRate, t: Int): Int

  final def nextFrame16(audioLength: AudioLength, samplingRate: SamplingRate, t: Int): Array[Short] = {
    val frame = Array.ofDim[Short](SampleCount(audioLength, samplingRate).value)
    var i = 0
    while (i < frame.length) {
      frame(i) = sample(samplingRate, t + i).toShort
      i += 1
    }
    frame
  }

}

object AudioGenerator {

  sealed abstract class Oscillator {

    protected def f(x: Double): Double

    final def osc(samplingRate: SamplingRate, t: Int, frequency: Double, volume: Double): Double = {
      val samples = samplingRate.value / frequency
      f(t % samples / samples) * volume
    }

  }

  object Oscillator {
    case object Sine extends Oscillator {
      def f(x: Double): Double = Math.sin(2.0 * Math.PI * x)
    }
    case object Sawtooth extends Oscillator {
      def f(x: Double): Double = 2.0 * (x - Math.floor(x + 0.5))
    }
  }

  def int(boolean: Boolean): Int = if (boolean) 1 else 0

  def playTones(t: Int, tones: Array[Double], samplingRate: SamplingRate, tempo: Int): Double = {
    // The duration of the tone in number of samples.
    val duration = samplingRate.value / tempo
    // A short pause between each tone played.
    val pause = samplingRate.value / 40

    val index = t % (tones.length * duration) / duration
    val previous =
      if (index == 0) {
        tones.length - 1
      } else {
        index - 1
      }
    tones(index) * int(t % duration > pause || tones(previous) == tones(index))
  }

  def playWhiteNoise(t: Int, tones: Array[Double], samplingRate: SamplingRate, tempo: Int, duration: Double): Double = {
    playTones(t, tones, samplingRate, tempo) * Math.random() * {
      if (t % (samplingRate.value / 8) > (samplingRate.value / 8 - (samplingRate.value * duration))) {
        1.0
      } else {
        0.0
      }
    }
  }

}
