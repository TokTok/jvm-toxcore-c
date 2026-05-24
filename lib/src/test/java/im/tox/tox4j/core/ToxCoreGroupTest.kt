package im.tox.tox4j.core

import im.tox.tox4j.core.callbacks.ToxCoreEventAdapter
import im.tox.tox4j.core.enums.ToxGroupPrivacyState
import im.tox.tox4j.core.enums.ToxGroupRole
import im.tox.tox4j.core.enums.ToxGroupTopicLock
import im.tox.tox4j.core.enums.ToxGroupVoiceState
import im.tox.tox4j.core.enums.ToxUserStatus
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxCoreImpl
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(kotlin.ExperimentalStdlibApi::class)
class ToxCoreGroupTest {
    private val options = ToxOptions(localDiscoveryEnabled = false)

    private fun createPrivateGroup(tox: ToxCoreImpl) =
        tox.groupNew(
            ToxGroupPrivacyState.PRIVATE,
            "ToxTest".encodeToByteArray(),
            "Founder".encodeToByteArray(),
        )

    @Test
    fun new_returnsZeroOnFirst() {
        ToxCoreImpl(options).use { tox ->
            assertEquals(0, createPrivateGroup(tox).value)
        }
    }

    @Test
    fun new_thenLeave_succeeds() {
        ToxCoreImpl(options).use { tox ->
            val g = createPrivateGroup(tox)
            tox.groupLeave(g, ByteArray(0))
        }
    }

    @Test
    fun newTwo_distinctNumbers() {
        ToxCoreImpl(options).use { tox ->
            val a = createPrivateGroup(tox)
            val b = createPrivateGroup(tox)
            assertEquals(0, a.value)
            assertEquals(1, b.value)
        }
    }

    @Test
    fun groupList_emptyInitially() {
        ToxCoreImpl(options).use { tox ->
            assertEquals(emptyList(), tox.groupList)
        }
    }

    @Test
    fun groupList_reflectsJoinedGroups() {
        ToxCoreImpl(options).use { tox ->
            val a = createPrivateGroup(tox)
            val b = createPrivateGroup(tox)
            assertEquals(listOf(a, b), tox.groupList)
            tox.groupLeave(a, ByteArray(0))
            // toxcore removes a group it left on its next iteration.
            tox.iterate(ToxCoreEventAdapter(), Unit)
            assertEquals(listOf(b), tox.groupList)
        }
    }

    @Test
    fun selfGetName_returnsCreatedName() {
        ToxCoreImpl(options).use { tox ->
            val g = createPrivateGroup(tox)
            assertContentEquals("Founder".encodeToByteArray(), tox.groupSelfGetName(g))
        }
    }

    @Test
    fun selfSetName_roundtrip() {
        ToxCoreImpl(options).use { tox ->
            val g = createPrivateGroup(tox)
            tox.groupSelfSetName(g, "Bob".encodeToByteArray())
            assertContentEquals("Bob".encodeToByteArray(), tox.groupSelfGetName(g))
        }
    }

    @Test
    fun selfGetStatus_isNoneByDefault() {
        ToxCoreImpl(options).use { tox ->
            val g = createPrivateGroup(tox)
            assertEquals(ToxUserStatus.NONE, tox.groupSelfGetStatus(g))
        }
    }

    @Test
    fun selfSetStatus_roundtrip() {
        ToxCoreImpl(options).use { tox ->
            val g = createPrivateGroup(tox)
            tox.groupSelfSetStatus(g, ToxUserStatus.BUSY)
            assertEquals(ToxUserStatus.BUSY, tox.groupSelfGetStatus(g))
        }
    }

    @Test
    fun selfGetRole_isFounder() {
        ToxCoreImpl(options).use { tox ->
            val g = createPrivateGroup(tox)
            assertEquals(ToxGroupRole.FOUNDER, tox.groupSelfGetRole(g))
        }
    }

    @Test
    fun selfGetPublicKey_hasExpectedSize() {
        ToxCoreImpl(options).use { tox ->
            val g = createPrivateGroup(tox)
            assertEquals(ToxCoreConstants.PUBLIC_KEY_SIZE, tox.groupSelfGetPublicKey(g).value.size)
        }
    }

    @Test
    fun getName_returnsCreatedGroupName() {
        ToxCoreImpl(options).use { tox ->
            val g = createPrivateGroup(tox)
            assertContentEquals("ToxTest".encodeToByteArray(), tox.groupGetName(g))
        }
    }

    @Test
    fun getChatId_hasExpectedLength() {
        ToxCoreImpl(options).use { tox ->
            val g = createPrivateGroup(tox)
            assertEquals(32, tox.groupGetChatId(g).value.size)
        }
    }

    @Test
    fun getPrivacyState_matchesCreated() {
        ToxCoreImpl(options).use { tox ->
            val g = createPrivateGroup(tox)
            assertEquals(ToxGroupPrivacyState.PRIVATE, tox.groupGetPrivacyState(g))
        }
    }

    @Test
    fun publicGroup_hasPublicPrivacyState() {
        ToxCoreImpl(options).use { tox ->
            val g =
                tox.groupNew(
                    ToxGroupPrivacyState.PUBLIC,
                    "Pub".encodeToByteArray(),
                    "Founder".encodeToByteArray(),
                )
            assertEquals(ToxGroupPrivacyState.PUBLIC, tox.groupGetPrivacyState(g))
        }
    }

    @Test
    fun getVoiceState_defaultIsAll() {
        ToxCoreImpl(options).use { tox ->
            val g = createPrivateGroup(tox)
            assertEquals(ToxGroupVoiceState.ALL, tox.groupGetVoiceState(g))
        }
    }

    @Test
    fun getTopicLock_defaultIsEnabled() {
        ToxCoreImpl(options).use { tox ->
            val g = createPrivateGroup(tox)
            assertEquals(ToxGroupTopicLock.ENABLED, tox.groupGetTopicLock(g))
        }
    }

    @Test
    fun getPeerLimit_isPositive() {
        ToxCoreImpl(options).use { tox ->
            val g = createPrivateGroup(tox)
            assertTrue(tox.groupGetPeerLimit(g) > 0)
        }
    }

    @Test
    fun setTopic_roundtrip() {
        ToxCoreImpl(options).use { tox ->
            val g = createPrivateGroup(tox)
            val topic = "Hello, world".encodeToByteArray()
            tox.groupSetTopic(g, topic)
            assertContentEquals(topic, tox.groupGetTopic(g))
        }
    }

    @Test
    fun setPassword_roundtrip() {
        ToxCoreImpl(options).use { tox ->
            val g = createPrivateGroup(tox)
            val pw = "secret".encodeToByteArray()
            tox.groupSetPassword(g, pw)
            assertContentEquals(pw, tox.groupGetPassword(g))
        }
    }

    @Test
    fun setVoiceState_roundtrip() {
        ToxCoreImpl(options).use { tox ->
            val g = createPrivateGroup(tox)
            tox.groupSetVoiceState(g, ToxGroupVoiceState.MODERATOR)
            assertEquals(ToxGroupVoiceState.MODERATOR, tox.groupGetVoiceState(g))
        }
    }

    @Test
    fun setTopicLock_roundtrip() {
        ToxCoreImpl(options).use { tox ->
            val g = createPrivateGroup(tox)
            tox.groupSetTopicLock(g, ToxGroupTopicLock.DISABLED)
            assertEquals(ToxGroupTopicLock.DISABLED, tox.groupGetTopicLock(g))
        }
    }
}
