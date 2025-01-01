package im.tox.tox4j.av.data

import kotlin.jvm.JvmInline

@JvmInline
value class BitRate(
    val value: Int,
) {
    companion object {
        val Unchanged: BitRate = BitRate(-1)
        val Disabled: BitRate = BitRate(0)
    }
}
