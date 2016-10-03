package im.tox.tox4j.av.callbacks.audio

import im.tox.tox4j.av.data.{ AudioLength, SampleCount, SamplingRate }
import jline.TerminalFactory
import org.scalatest.FunSuite

@SuppressWarnings(Array("org.wartremover.warts.While"))
final class AudioPlaybackShow extends FunSuite {

  val audio = AudioGenerators.default

  val samplingRate = SamplingRate.Rate24k
  val audioLength = AudioLength.Length60
  val frameSize = SampleCount(audioLength, samplingRate)
  val playback = new AudioPlayback(samplingRate)

  test("main") {
    val terminalWidth = TerminalFactory.get.getWidth

    System.out.print("\u001b[2J")

    for (t <- 0 to audio.length(samplingRate) by frameSize.value) {
      val frame = audio.nextFrame16(audioLength, samplingRate, t)
      System.out.print("\u001b[H")
      System.out.println(AudioPlayback.showWave(frame, terminalWidth))
      System.out.println(s"t=$t")
      System.out.flush()
      playback.play(frame)
    }

    while (!playback.done(audio.length(samplingRate))) {
      Thread.sleep(100)
    }
  }

}
