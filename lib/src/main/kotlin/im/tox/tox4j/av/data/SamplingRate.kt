package im.tox.tox4j.av.data

enum class SamplingRate(
    val value: Int,
) {
    Rate8k(8000),
    Rate12k(12000),
    Rate16k(16000),
    Rate24k(24000),
    Rate48k(48000),
}
