package im.tox.tox4j.av.data

import kotlin.jvm.JvmInline

/** A video frame's height in pixels. */
@JvmInline
value class ToxAvHeight(
    val value: Int,
) {
    init {
        require(value in 0..0xFFFF) { "ToxAvHeight must fit in uint16_t, got $value" }
    }
}
