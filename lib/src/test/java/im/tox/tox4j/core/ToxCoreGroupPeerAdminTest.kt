package im.tox.tox4j.core

import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.data.ToxGroupChatId
import im.tox.tox4j.core.data.ToxGroupNumber
import im.tox.tox4j.core.data.ToxGroupPeerNumber
import im.tox.tox4j.core.enums.ToxGroupPrivacyState
import im.tox.tox4j.core.enums.ToxGroupRole
import im.tox.tox4j.core.enums.ToxMessageType
import im.tox.tox4j.core.exceptions.ToxGroupDisconnectException
import im.tox.tox4j.core.exceptions.ToxGroupInviteAcceptException
import im.tox.tox4j.core.exceptions.ToxGroupInviteFriendException
import im.tox.tox4j.core.exceptions.ToxGroupIsConnectedException
import im.tox.tox4j.core.exceptions.ToxGroupJoinException
import im.tox.tox4j.core.exceptions.ToxGroupKickPeerException
import im.tox.tox4j.core.exceptions.ToxGroupPeerQueryException
import im.tox.tox4j.core.exceptions.ToxGroupSelfQueryException
import im.tox.tox4j.core.exceptions.ToxGroupSendCustomPacketException
import im.tox.tox4j.core.exceptions.ToxGroupSendCustomPrivatePacketException
import im.tox.tox4j.core.exceptions.ToxGroupSendMessageException
import im.tox.tox4j.core.exceptions.ToxGroupSendPrivateMessageException
import im.tox.tox4j.core.exceptions.ToxGroupSetIgnoreException
import im.tox.tox4j.core.exceptions.ToxGroupSetPeerLimitException
import im.tox.tox4j.core.exceptions.ToxGroupSetRoleException
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxCoreImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ToxCoreGroupPeerAdminTest {
    private val options = ToxOptions(localDiscoveryEnabled = false)

    private inline fun withGroup(block: (ToxCoreImpl, ToxGroupNumber) -> Unit) {
        ToxCoreImpl(options).use { tox ->
            val group =
                tox.groupNew(
                    ToxGroupPrivacyState.PRIVATE,
                    "SmokeTest".encodeToByteArray(),
                    "Founder".encodeToByteArray(),
                )
            block(tox, group)
        }
    }

    @Test
    fun groupJoin_invalidChatId_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxGroupJoinException> {
                    tox.groupJoin(
                        ToxGroupChatId(ByteArray(ToxCoreConstants.GROUP_CHAT_ID_SIZE)),
                        "Joiner".encodeToByteArray(),
                        ByteArray(0),
                    )
                }
            // CORE is c-toxcore's catch-all join error.
            assertTrue(ex.code in setOf(ToxGroupJoinException.Code.CORE, ToxGroupJoinException.Code.INIT))
        }
    }

    @Test
    fun groupIsConnected_freshGroup_isTrue() {
        withGroup { tox, group ->
            // True at once: it reports the intent to connect, not peers.
            assertTrue(tox.groupIsConnected(group))
        }
    }

    @Test
    fun groupIsConnected_unknownGroup_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxGroupIsConnectedException> {
                    tox.groupIsConnected(ToxGroupNumber(999))
                }
            assertEquals(ToxGroupIsConnectedException.Code.GROUP_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun groupDisconnect_unknownGroup_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxGroupDisconnectException> {
                    tox.groupDisconnect(ToxGroupNumber(999))
                }
            assertEquals(ToxGroupDisconnectException.Code.GROUP_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun groupDisconnect_validGroup_succeeds() {
        withGroup { tox, group ->
            tox.groupDisconnect(group)
        }
    }

    @Test
    fun groupSelfGetPeerId_freshGroup_returnsValid() {
        withGroup { tox, group ->
            tox.groupSelfGetPeerId(group)
        }
    }

    @Test
    fun groupSelfGetPeerId_unknownGroup_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxGroupSelfQueryException> {
                    tox.groupSelfGetPeerId(ToxGroupNumber(999))
                }
            assertEquals(ToxGroupSelfQueryException.Code.GROUP_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun groupPeerGetName_self_returnsFounderName() {
        withGroup { tox, group ->
            val selfId = tox.groupSelfGetPeerId(group)
            assertEquals("Founder", tox.groupPeerGetName(group, selfId).decodeToString())
        }
    }

    @Test
    fun groupPeerGetName_unknownPeer_throws() {
        withGroup { tox, group ->
            val ex =
                assertFailsWith<ToxGroupPeerQueryException> {
                    tox.groupPeerGetName(group, ToxGroupPeerNumber(999))
                }
            assertEquals(ToxGroupPeerQueryException.Code.PEER_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun groupPeerGetPublicKey_unknownPeer_throws() {
        withGroup { tox, group ->
            val ex =
                assertFailsWith<ToxGroupPeerQueryException> {
                    tox.groupPeerGetPublicKey(group, ToxGroupPeerNumber(999))
                }
            assertEquals(ToxGroupPeerQueryException.Code.PEER_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun groupPeerGetStatus_unknownPeer_throws() {
        withGroup { tox, group ->
            val ex =
                assertFailsWith<ToxGroupPeerQueryException> {
                    tox.groupPeerGetStatus(group, ToxGroupPeerNumber(999))
                }
            assertEquals(ToxGroupPeerQueryException.Code.PEER_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun groupPeerGetRole_unknownPeer_throws() {
        withGroup { tox, group ->
            val ex =
                assertFailsWith<ToxGroupPeerQueryException> {
                    tox.groupPeerGetRole(group, ToxGroupPeerNumber(999))
                }
            assertEquals(ToxGroupPeerQueryException.Code.PEER_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun groupPeerGetConnectionStatus_unknownPeer_throws() {
        withGroup { tox, group ->
            val ex =
                assertFailsWith<ToxGroupPeerQueryException> {
                    tox.groupPeerGetConnectionStatus(group, ToxGroupPeerNumber(999))
                }
            assertEquals(ToxGroupPeerQueryException.Code.PEER_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun groupSetPeerLimit_validGroup_succeeds() {
        withGroup { tox, group ->
            tox.groupSetPeerLimit(group, 64)
        }
    }

    @Test
    fun groupSetPeerLimit_unknownGroup_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxGroupSetPeerLimitException> {
                    tox.groupSetPeerLimit(ToxGroupNumber(999), 64)
                }
            assertEquals(ToxGroupSetPeerLimitException.Code.GROUP_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun groupSetIgnore_selfPeer_throwsSelf() {
        withGroup { tox, group ->
            val selfId = tox.groupSelfGetPeerId(group)
            val ex =
                assertFailsWith<ToxGroupSetIgnoreException> {
                    tox.groupSetIgnore(group, selfId, true)
                }
            assertEquals(ToxGroupSetIgnoreException.Code.SELF, ex.code)
        }
    }

    @Test
    fun groupSetIgnore_unknownGroup_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxGroupSetIgnoreException> {
                    tox.groupSetIgnore(ToxGroupNumber(999), ToxGroupPeerNumber(0), true)
                }
            assertEquals(ToxGroupSetIgnoreException.Code.GROUP_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun groupSetRole_selfPeer_throwsSelf() {
        withGroup { tox, group ->
            val selfId = tox.groupSelfGetPeerId(group)
            val ex =
                assertFailsWith<ToxGroupSetRoleException> {
                    tox.groupSetRole(group, selfId, ToxGroupRole.USER)
                }
            assertEquals(ToxGroupSetRoleException.Code.SELF, ex.code)
        }
    }

    @Test
    fun groupSetRole_unknownGroup_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxGroupSetRoleException> {
                    tox.groupSetRole(ToxGroupNumber(999), ToxGroupPeerNumber(0), ToxGroupRole.USER)
                }
            assertEquals(ToxGroupSetRoleException.Code.GROUP_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun groupKickPeer_selfPeer_throwsSelf() {
        withGroup { tox, group ->
            val selfId = tox.groupSelfGetPeerId(group)
            val ex =
                assertFailsWith<ToxGroupKickPeerException> {
                    tox.groupKickPeer(group, selfId)
                }
            assertEquals(ToxGroupKickPeerException.Code.SELF, ex.code)
        }
    }

    @Test
    fun groupKickPeer_unknownGroup_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxGroupKickPeerException> {
                    tox.groupKickPeer(ToxGroupNumber(999), ToxGroupPeerNumber(0))
                }
            assertEquals(ToxGroupKickPeerException.Code.GROUP_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun groupSendMessage_unknownGroup_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxGroupSendMessageException> {
                    tox.groupSendMessage(
                        ToxGroupNumber(999),
                        ToxMessageType.NORMAL,
                        "hi".encodeToByteArray(),
                    )
                }
            assertEquals(ToxGroupSendMessageException.Code.GROUP_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun groupSendMessage_emptyMessage_throws() {
        withGroup { tox, group ->
            val ex =
                assertFailsWith<ToxGroupSendMessageException> {
                    tox.groupSendMessage(
                        group,
                        ToxMessageType.NORMAL,
                        ByteArray(0),
                    )
                }
            assertEquals(ToxGroupSendMessageException.Code.EMPTY, ex.code)
        }
    }

    @Test
    fun groupSendPrivateMessage_unknownPeer_throws() {
        withGroup { tox, group ->
            val ex =
                assertFailsWith<ToxGroupSendPrivateMessageException> {
                    tox.groupSendPrivateMessage(
                        group,
                        ToxGroupPeerNumber(999),
                        ToxMessageType.NORMAL,
                        "hi".encodeToByteArray(),
                    )
                }
            assertEquals(ToxGroupSendPrivateMessageException.Code.PEER_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun groupSendCustomPacket_unknownGroup_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxGroupSendCustomPacketException> {
                    tox.groupSendCustomPacket(ToxGroupNumber(999), true, "hi".encodeToByteArray())
                }
            assertEquals(ToxGroupSendCustomPacketException.Code.GROUP_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun groupSendCustomPrivatePacket_unknownPeer_throws() {
        withGroup { tox, group ->
            val ex =
                assertFailsWith<ToxGroupSendCustomPrivatePacketException> {
                    tox.groupSendCustomPrivatePacket(
                        group,
                        ToxGroupPeerNumber(999),
                        true,
                        "hi".encodeToByteArray(),
                    )
                }
            assertEquals(ToxGroupSendCustomPrivatePacketException.Code.PEER_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun groupInviteFriend_unknownGroup_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxGroupInviteFriendException> {
                    tox.groupInviteFriend(ToxGroupNumber(999), ToxFriendNumber(0))
                }
            assertEquals(ToxGroupInviteFriendException.Code.GROUP_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun groupInviteFriend_unknownFriend_throws() {
        withGroup { tox, group ->
            val ex =
                assertFailsWith<ToxGroupInviteFriendException> {
                    tox.groupInviteFriend(group, ToxFriendNumber(999))
                }
            assertEquals(ToxGroupInviteFriendException.Code.FRIEND_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun groupInviteAccept_badInvite_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxGroupInviteAcceptException> {
                    tox.groupInviteAccept(
                        ToxFriendNumber(0),
                        ByteArray(0),
                        "Me".encodeToByteArray(),
                        ByteArray(0),
                    )
                }
            // gc_accept_invite checks the length before the friend number.
            assertEquals(ToxGroupInviteAcceptException.Code.BAD_INVITE, ex.code)
        }
    }
}
