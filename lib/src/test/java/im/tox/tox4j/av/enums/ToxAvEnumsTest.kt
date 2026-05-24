package im.tox.tox4j.av.enums

import kotlin.test.Test
import kotlin.test.assertEquals

/** The ToxAV side of `ToxEnumsTest`. */
class ToxAvEnumsTest {
    private inline fun <reified T : Enum<T>> assertValues(vararg expected: T) {
        val actual = enumValues<T>().toList()
        assertEquals(expected.toList(), actual)
    }

    @Test
    fun toxavCallControl() =
        assertValues(
            ToxAvCallControl.RESUME,
            ToxAvCallControl.PAUSE,
            ToxAvCallControl.CANCEL,
            ToxAvCallControl.MUTE_AUDIO,
            ToxAvCallControl.UNMUTE_AUDIO,
            ToxAvCallControl.HIDE_VIDEO,
            ToxAvCallControl.SHOW_VIDEO,
        )

    // Bit flags with the C values, so a change here breaks the C ABI.
    @Test
    fun toxavFriendCallState() {
        assertEquals(0, ToxAvFriendCallState.NONE)
        assertEquals(1, ToxAvFriendCallState.ERROR)
        assertEquals(2, ToxAvFriendCallState.FINISHED)
        assertEquals(4, ToxAvFriendCallState.SENDING_A)
        assertEquals(8, ToxAvFriendCallState.SENDING_V)
        assertEquals(16, ToxAvFriendCallState.ACCEPTING_A)
        assertEquals(32, ToxAvFriendCallState.ACCEPTING_V)
    }
}
