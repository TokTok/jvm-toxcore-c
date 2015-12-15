package im.tox.tox4j.av.callbacks

import jline.TerminalFactory
import org.scalatest.FunSuite

final class AudioPlaybackTest extends FunSuite {

  val displayWave = !sys.env.contains("TRAVIS")

  val samplingRate = 48000
  val audio = AudioGenerator(samplingRate)
  val playback = new AudioPlayback(samplingRate)

  test("main") {
    val terminalWidth = TerminalFactory.get.getWidth
    val frameSize = samplingRate / 8

    if (displayWave) {
      System.out.print("\u001b[2J")
    }

    for (t <- 0 to audio.length by frameSize) {
      val frame = audio.nextFrame16(t, frameSize)
      if (displayWave) {
        System.out.print("\u001b[H")
        System.out.println(AudioPlayback.showWave(frame, terminalWidth))
        System.out.println(s"t=$t")
        System.out.flush()
      }
      playback.play(frame)
    }

    while (!playback.done(audio.length)) {
      Thread.sleep(100)
    }
  }

}
