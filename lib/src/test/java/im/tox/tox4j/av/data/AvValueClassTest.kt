package im.tox.tox4j.av.data

import im.tox.tox4j.core.data.ToxPort
import kotlin.test.Test
import kotlin.test.assertFailsWith

/** C has `width`, `height` and `port` as `uint16_t`, which an `Int` holds more than. */
class AvValueClassTest {
    @Test
    fun uint16_units_accept_zero_and_max_uint16() {
        for (value in listOf(0, 0xFFFF)) {
            ToxAvWidth(value)
            ToxAvHeight(value)
            ToxPort(value)
        }
    }

    @Test
    fun uint16_units_reject_negative_and_above_uint16() {
        for (value in listOf(-1, 0x10000)) {
            assertFailsWith<IllegalArgumentException> { ToxAvWidth(value) }
            assertFailsWith<IllegalArgumentException> { ToxAvHeight(value) }
            assertFailsWith<IllegalArgumentException> { ToxPort(value) }
        }
    }
}
