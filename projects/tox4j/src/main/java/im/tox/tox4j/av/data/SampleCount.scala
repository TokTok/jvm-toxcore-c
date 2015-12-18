package im.tox.tox4j.av.data

final case class SampleCount private (value: Int) extends AnyVal

case object SampleCount extends ((AudioLength, SamplingRate) => SampleCount) {

  override def apply(audioLength: AudioLength, samplingRate: SamplingRate): SampleCount = {
    new SampleCount(samplingRate.value / 1000 * audioLength.value.toMillis.toInt)
  }

}
