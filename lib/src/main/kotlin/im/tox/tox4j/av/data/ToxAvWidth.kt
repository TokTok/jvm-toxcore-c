package im.tox.tox4j.av.data

import kotlin.jvm.JvmInline

/** A video frame's width in pixels. */
@JvmInline
value class ToxAvWidth(
    val value: Int,
) {
    init {
        require(value in 0..0xFFFF) { "ToxAvWidth must fit in uint16_t, got $value" }
    }
}
