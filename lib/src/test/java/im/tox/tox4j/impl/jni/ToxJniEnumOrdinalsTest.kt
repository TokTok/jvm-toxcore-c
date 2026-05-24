package im.tox.tox4j.impl.jni

import im.tox.tox4j.av.enums.ToxAvCallControl
import im.tox.tox4j.core.enums.ToxConferenceType
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.core.enums.ToxFileControl
import im.tox.tox4j.core.enums.ToxFileKind
import im.tox.tox4j.core.enums.ToxGroupExitType
import im.tox.tox4j.core.enums.ToxGroupJoinFail
import im.tox.tox4j.core.enums.ToxGroupModEvent
import im.tox.tox4j.core.enums.ToxGroupPrivacyState
import im.tox.tox4j.core.enums.ToxGroupRole
import im.tox.tox4j.core.enums.ToxGroupTopicLock
import im.tox.tox4j.core.enums.ToxGroupVoiceState
import im.tox.tox4j.core.enums.ToxLogLevel
import im.tox.tox4j.core.enums.ToxMessageType
import im.tox.tox4j.core.enums.ToxProxyType
import im.tox.tox4j.core.enums.ToxSavedataType
import im.tox.tox4j.core.enums.ToxUserStatus
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

/** The C++ `Enum::valueOf<T>` switches map these ordinals to C values. */
class ToxJniEnumOrdinalsTest {
    private fun <E : Enum<E>> assertOrdinalOrder(
        values: Array<E>,
        expected: List<String>,
    ) = assertContentEquals(expected, values.map { it.name })

    // ToxCore

    @Test
    fun toxConferenceType_order() = assertOrdinalOrder(ToxConferenceType.values(), listOf("TEXT", "AV"))

    @Test
    fun toxConnection_order() = assertOrdinalOrder(ToxConnection.values(), listOf("NONE", "TCP", "UDP"))

    @Test
    fun toxFileControl_order() = assertOrdinalOrder(ToxFileControl.values(), listOf("RESUME", "PAUSE", "CANCEL"))

    @Test
    fun toxFileKind_constants() {
        assertEquals(0, ToxFileKind.DATA)
        assertEquals(1, ToxFileKind.AVATAR)
        assertEquals(2, ToxFileKind.STICKER)
        assertEquals(3, ToxFileKind.SHA1)
        assertEquals(4, ToxFileKind.SHA256)
    }

    @Test
    fun toxGroupExitType_order() =
        assertOrdinalOrder(
            ToxGroupExitType.values(),
            listOf(
                "QUIT",
                "TIMEOUT",
                "DISCONNECTED",
                "SELF_DISCONNECTED",
                "KICK",
                "SYNC_ERROR",
            ),
        )

    @Test
    fun toxGroupJoinFail_order() =
        assertOrdinalOrder(
            ToxGroupJoinFail.values(),
            listOf("PEER_LIMIT", "INVALID_PASSWORD", "UNKNOWN"),
        )

    @Test
    fun toxGroupModEvent_order() =
        assertOrdinalOrder(
            ToxGroupModEvent.values(),
            listOf("KICK", "OBSERVER", "USER", "MODERATOR"),
        )

    @Test
    fun toxGroupPrivacyState_order() = assertOrdinalOrder(ToxGroupPrivacyState.values(), listOf("PUBLIC", "PRIVATE"))

    @Test
    fun toxGroupRole_order() =
        assertOrdinalOrder(
            ToxGroupRole.values(),
            listOf("FOUNDER", "MODERATOR", "USER", "OBSERVER"),
        )

    @Test
    fun toxGroupTopicLock_order() = assertOrdinalOrder(ToxGroupTopicLock.values(), listOf("ENABLED", "DISABLED"))

    @Test
    fun toxGroupVoiceState_order() =
        assertOrdinalOrder(
            ToxGroupVoiceState.values(),
            listOf("ALL", "MODERATOR", "FOUNDER"),
        )

    @Test
    fun toxLogLevel_order() =
        assertOrdinalOrder(
            ToxLogLevel.values(),
            listOf("TRACE", "DEBUG", "INFO", "WARNING", "ERROR"),
        )

    @Test
    fun toxMessageType_order() = assertOrdinalOrder(ToxMessageType.values(), listOf("NORMAL", "ACTION"))

    @Test
    fun toxProxyType_order() = assertOrdinalOrder(ToxProxyType.values(), listOf("NONE", "HTTP", "SOCKS5"))

    @Test
    fun toxSavedataType_order() =
        assertOrdinalOrder(
            ToxSavedataType.values(),
            listOf("NONE", "TOX_SAVE", "SECRET_KEY"),
        )

    @Test
    fun toxUserStatus_order() = assertOrdinalOrder(ToxUserStatus.values(), listOf("NONE", "AWAY", "BUSY"))

    // ToxAv

    @Test
    fun toxavCallControl_order() =
        assertOrdinalOrder(
            ToxAvCallControl.values(),
            listOf(
                "RESUME",
                "PAUSE",
                "CANCEL",
                "MUTE_AUDIO",
                "UNMUTE_AUDIO",
                "HIDE_VIDEO",
                "SHOW_VIDEO",
            ),
        )
}
