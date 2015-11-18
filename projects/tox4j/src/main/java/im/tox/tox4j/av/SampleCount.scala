package im.tox.tox4j.av

final class SampleCount private (val value: Int) extends AnyVal

object SampleCount extends ((AudioLength, SamplingRate) => SampleCount) {

  def unsafeFromInt(value: Int): SampleCount = new SampleCount(value)

  override def apply(audioLength: AudioLength, samplingRate: SamplingRate): SampleCount = {
    new SampleCount(samplingRate.value / 1000 * audioLength.value.toMicros.toInt / 1000)
  }

}
