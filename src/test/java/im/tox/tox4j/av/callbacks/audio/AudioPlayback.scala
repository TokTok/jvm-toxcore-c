package im.tox.tox4j.av.callbacks.audio

import javax.sound.sampled._

import im.tox.tox4j.av.data.SamplingRate

import scala.util.Try

object AudioPlayback {

  def showWave(pcm: Array[Short], width: Int): String = {
    val height = width / 10

    val screen = (0 until height).map(_ => Array.fill[Char](width)(' '))

    val maxSampleValue = -Short.MinValue
    for ((sample, x) <- pcm.zipWithIndex) {
      val y = valueToRange(sample + maxSampleValue, maxSampleValue * 2, height)
      screen(y)(valueToRange(x, pcm.length, width)) = '#'
    }

    screen.map(new String(_)).mkString("\n")
  }

  private def valueToRange(value: Double, maxValue: Int, maxRange: Int): Int = {
    (value / maxValue * maxRange).toInt
  }

  private def serialiseAudioFrame(pcm: Array[Short]): Array[Byte] = {
    val buffer = Array.ofDim[Byte](pcm.length * 2)
    for (i <- buffer.indices by 2) {
      buffer(i) = (pcm(i / 2) >> 8).toByte
      buffer(i + 1) = pcm(i / 2).toByte
    }

    buffer
  }

}

final class AudioPlayback(samplingRate: SamplingRate) {

  def play(pcm: Array[Short]): Unit = {
    soundLine.foreach { soundLine =>
      val buffer = AudioPlayback.serialiseAudioFrame(pcm)
      soundLine.write(buffer, 0, buffer.length)
    }
  }

  def done(length: Int): Boolean = {
    soundLine.toOption.map(_.getLongFramePosition >= length).getOrElse(true)
  }

  private val soundLine = Try {
    val format = new AudioFormat(samplingRate.value, 16, 1, true, true)
    val info = new DataLine.Info(classOf[SourceDataLine], format)
    val soundLine = AudioSystem.getLine(info).asInstanceOf[SourceDataLine]
    soundLine.open(format, samplingRate.value)
    soundLine.start()
    soundLine
  }

}
