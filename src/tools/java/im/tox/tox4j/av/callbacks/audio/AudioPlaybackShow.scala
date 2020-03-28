package im.tox.tox4j.av.callbacks.audio

import im.tox.tox4j.av.data.{ AudioLength, SampleCount, SamplingRate }

@SuppressWarnings(Array("org.wartremover.warts.While"))
object AudioPlaybackShow {

  private val audio = AudioGenerators.default

  private val samplingRate = SamplingRate.Rate24k
  private val audioLength = AudioLength.Length60
  private val frameSize = SampleCount(audioLength, samplingRate)
  private val playback = new AudioPlayback(samplingRate)

  def main(args: Array[String]) {
    val terminalWidth = 190

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
