package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/** A TCP or UDP port number. */
@JvmInline
value class ToxPort(
    val value: Int,
) {
    init {
        require(value in 0..0xFFFF) { "ToxPort must fit in uint16_t, got $value" }
    }
}
