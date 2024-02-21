package im.tox.tox4j.av.data

/** Length in microseconds. */
@Suppress("ktlint:standard:enum-entry-name-case")
enum class AudioLength(val value: Int) {
    Length2_5(2500),
    Length5(5000),
    Length10(10000),
    Length20(20000),
    Length40(40000),
    Length60(60000),
    ;

    fun toMillis(): Double = value * 0.001
}
