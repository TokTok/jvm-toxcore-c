package im.tox.tox4j.av.callbacks

import javax.sound.sampled.{AudioSystem, SourceDataLine, DataLine, AudioFormat}

import scala.util.Try

object AudioPlayback {

  def showWave(pcm: Array[Short], width: Int): String = {
    val height = width / 12

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

  def play(pcm: Array[Short]): Unit = {
    soundLine.foreach { soundLine =>
      val buffer = serialiseAudioFrame(pcm)
      soundLine.write(buffer, 0, buffer.length)
    }
  }

  private def serialiseAudioFrame(pcm: Array[Short]): Array[Byte] = {
    val buffer = Array.ofDim[Byte](pcm.length * 2)
    for (i <- buffer.indices by 2) {
      buffer(i) = (pcm(i / 2) >> 8).toByte
      buffer(i + 1) = pcm(i / 2).toByte
    }

    buffer
  }

  private def soundLine = Try {
    val format = new AudioFormat(8000f, 16, 1, true, true)
    val info = new DataLine.Info(classOf[SourceDataLine], format)
    val soundLine = AudioSystem.getLine(info).asInstanceOf[SourceDataLine]
    soundLine.open(format, 32000)
    soundLine.start()
    soundLine
  }

  def main(args: Array[String]): Unit = {
    soundLine.foreach { soundLine =>
      var t = 0

      while (t < AudioGenerator.Selected.length) {
        val frame = AudioGenerator.Selected.nextFrame16(t, 80)
        val buffer = serialiseAudioFrame(frame)
        t += 80
        soundLine.write(buffer, 0, buffer.length)
      }
    }
  }

}

