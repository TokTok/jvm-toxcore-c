package im.tox.tox4j.core.enums

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The C++ side maps Kotlin ordinals to C values, so a reordered enum breaks
 * silently. `ToxFileKind` isn't an enum, so it's not here.
 */
class ToxEnumsTest {
    private inline fun <reified T : Enum<T>> assertValues(vararg expected: T) {
        val actual = enumValues<T>().toList()
        assertEquals(expected.toList(), actual)
    }

    @Test
    fun toxConferenceType() =
        assertValues(
            ToxConferenceType.TEXT,
            ToxConferenceType.AV,
        )

    @Test
    fun toxConnection() =
        assertValues(
            ToxConnection.NONE,
            ToxConnection.TCP,
            ToxConnection.UDP,
        )

    @Test
    fun toxFileControl() =
        assertValues(
            ToxFileControl.RESUME,
            ToxFileControl.PAUSE,
            ToxFileControl.CANCEL,
        )

    @Test
    fun toxGroupExitType() =
        assertValues(
            ToxGroupExitType.QUIT,
            ToxGroupExitType.TIMEOUT,
            ToxGroupExitType.DISCONNECTED,
            ToxGroupExitType.SELF_DISCONNECTED,
            ToxGroupExitType.KICK,
            ToxGroupExitType.SYNC_ERROR,
        )

    @Test
    fun toxGroupJoinFail() =
        assertValues(
            ToxGroupJoinFail.PEER_LIMIT,
            ToxGroupJoinFail.INVALID_PASSWORD,
            ToxGroupJoinFail.UNKNOWN,
        )

    @Test
    fun toxGroupModEvent() =
        assertValues(
            ToxGroupModEvent.KICK,
            ToxGroupModEvent.OBSERVER,
            ToxGroupModEvent.USER,
            ToxGroupModEvent.MODERATOR,
        )

    @Test
    fun toxGroupPrivacyState() =
        assertValues(
            ToxGroupPrivacyState.PUBLIC,
            ToxGroupPrivacyState.PRIVATE,
        )

    @Test
    fun toxGroupRole() =
        assertValues(
            ToxGroupRole.FOUNDER,
            ToxGroupRole.MODERATOR,
            ToxGroupRole.USER,
            ToxGroupRole.OBSERVER,
        )

    @Test
    fun toxGroupTopicLock() =
        assertValues(
            ToxGroupTopicLock.ENABLED,
            ToxGroupTopicLock.DISABLED,
        )

    @Test
    fun toxGroupVoiceState() =
        assertValues(
            ToxGroupVoiceState.ALL,
            ToxGroupVoiceState.MODERATOR,
            ToxGroupVoiceState.FOUNDER,
        )

    @Test
    fun toxMessageType() =
        assertValues(
            ToxMessageType.NORMAL,
            ToxMessageType.ACTION,
        )

    @Test
    fun toxProxyType() =
        assertValues(
            ToxProxyType.NONE,
            ToxProxyType.HTTP,
            ToxProxyType.SOCKS5,
        )

    @Test
    fun toxSavedataType() =
        assertValues(
            ToxSavedataType.NONE,
            ToxSavedataType.TOX_SAVE,
            ToxSavedataType.SECRET_KEY,
        )

    @Test
    fun toxUserStatus() =
        assertValues(
            ToxUserStatus.NONE,
            ToxUserStatus.AWAY,
            ToxUserStatus.BUSY,
        )

    @Test
    fun toxFileKindData() = assertEquals(0, ToxFileKind.DATA)

    @Test
    fun toxFileKindAvatar() = assertEquals(1, ToxFileKind.AVATAR)
}
