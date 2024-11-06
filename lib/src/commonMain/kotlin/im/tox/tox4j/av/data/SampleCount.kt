package im.tox.tox4j.av.data

import kotlin.jvm.JvmInline

@JvmInline
value class SampleCount(
    val value: Int,
) {
    constructor(
        audioLength: AudioLength,
        samplingRate: SamplingRate,
    ) : this((samplingRate.value / 1000 * audioLength.toMillis()).toInt())
}
