package im.tox.tox4j.av.data

@JvmInline
value class SampleCount(val value: Int) {
  constructor(
      audioLength: AudioLength,
      samplingRate: SamplingRate
  ) : this((samplingRate.value / 1000 * audioLength.toMillis()).toInt())
}
