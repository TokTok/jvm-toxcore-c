package im.tox.tox4j.av.callbacks.audio

import im.tox.tox4j.av.data.{AudioLength, SampleCount, SamplingRate}
import jline.TerminalFactory
import org.scalatest.FunSuite

final class AudioPlaybackTest extends FunSuite {

  val displayWave = !sys.env.contains("TRAVIS")

  val samplingRate = SamplingRate.Rate48k
  val audioLength = AudioLength.Length60
  val frameSize = SampleCount(audioLength, samplingRate)
  val audio = AudioGenerators.default
  val playback = new AudioPlayback(samplingRate)

  test("main") {
    val terminalWidth = TerminalFactory.get.getWidth

    if (displayWave) {
      System.out.print("\u001b[2J")
    }

    for (t <- 0 to audio.length(samplingRate) by frameSize.value) {
      val frame = audio.nextFrame16(audioLength, samplingRate, t)
      if (displayWave) {
        System.out.print("\u001b[H")
        System.out.println(AudioPlayback.showWave(frame, terminalWidth))
        System.out.println(s"t=$t")
        System.out.flush()
      }
      playback.play(frame)
    }

    while (!playback.done(audio.length(samplingRate))) {
      Thread.sleep(100)
    }
  }

}
