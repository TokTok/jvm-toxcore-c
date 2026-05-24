package im.tox.tox4j.core

import im.tox.tox4j.core.data.ToxAddress
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.data.ToxPublicKey
import im.tox.tox4j.core.exceptions.ToxFriendAddException
import im.tox.tox4j.core.exceptions.ToxFriendByPublicKeyException
import im.tox.tox4j.core.exceptions.ToxFriendDeleteException
import im.tox.tox4j.core.exceptions.ToxFriendGetLastOnlineException
import im.tox.tox4j.core.exceptions.ToxFriendGetPublicKeyException
import im.tox.tox4j.core.exceptions.ToxFriendQueryException
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxCoreImpl
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(kotlin.ExperimentalStdlibApi::class)
class ToxCoreFriendTest {
    private val options = ToxOptions(localDiscoveryEnabled = false)

    private inline fun withPeerKey(block: (ToxCoreImpl, ToxPublicKey) -> Unit) {
        ToxCoreImpl(options).use { peer ->
            ToxCoreImpl(options).use { tox ->
                block(tox, peer.publicKey)
            }
        }
    }

    @Test
    fun addFriendNorequest_returnsFriendNumber() {
        withPeerKey { tox, key ->
            val n = tox.friendAddNorequest(key)
            assertEquals(0, n.value)
        }
    }

    @Test
    fun addFriendNorequest_friendExists() {
        withPeerKey { tox, key ->
            val n = tox.friendAddNorequest(key)
            assertTrue(tox.friendExists(n))
        }
    }

    @Test
    fun friendExists_falseForUnknownNumber() {
        ToxCoreImpl(options).use { tox ->
            assertFalse(tox.friendExists(ToxFriendNumber(0)))
            assertFalse(tox.friendExists(ToxFriendNumber(999)))
        }
    }

    @Test
    fun friendList_includesAddedFriend() {
        withPeerKey { tox, key ->
            val n = tox.friendAddNorequest(key)
            assertEquals(listOf(n), tox.friendList)
        }
    }

    @Test
    fun friendList_empty_afterDelete() {
        withPeerKey { tox, key ->
            val n = tox.friendAddNorequest(key)
            tox.friendDelete(n)
            assertEquals(0, tox.friendList.size)
            assertFalse(tox.friendExists(n))
        }
    }

    @Test
    fun friendByPublicKey_returnsAddedFriend() {
        withPeerKey { tox, key ->
            val n = tox.friendAddNorequest(key)
            assertEquals(n.value, tox.friendByPublicKey(key).value)
        }
    }

    @Test
    fun friendByPublicKey_unknown_throws() {
        ToxCoreImpl(options).use { tox ->
            val randomKey = ToxPublicKey(ByteArray(ToxCoreConstants.PUBLIC_KEY_SIZE))
            val ex =
                assertFailsWith<ToxFriendByPublicKeyException> {
                    tox.friendByPublicKey(randomKey)
                }
            assertEquals(ToxFriendByPublicKeyException.Code.NOT_FOUND, ex.code)
        }
    }

    @Test
    fun getFriendPublicKey_roundtrip() {
        withPeerKey { tox, key ->
            val n = tox.friendAddNorequest(key)
            assertContentEquals(key.value, tox.friendGetPublicKey(n).value)
        }
    }

    @Test
    fun getFriendPublicKey_unknownFriend_throws() {
        ToxCoreImpl(options).use { tox ->
            assertFailsWith<ToxFriendGetPublicKeyException> {
                tox.friendGetPublicKey(ToxFriendNumber(999))
            }
        }
    }

    @Test
    fun friendGetTyping_freshlyAddedFriend_isFalse() {
        withPeerKey { tox, key ->
            val n = tox.friendAddNorequest(key)
            assertFalse(tox.friendGetTyping(n))
        }
    }

    @Test
    fun friendGetTyping_unknownFriend_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxFriendQueryException> {
                    tox.friendGetTyping(ToxFriendNumber(999))
                }
            assertEquals(ToxFriendQueryException.Code.FRIEND_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun friendGetLastOnline_freshlyAddedFriend_returnsZero() {
        withPeerKey { tox, key ->
            val n = tox.friendAddNorequest(key)
            // 0 means never seen.
            assertEquals(0L, tox.friendGetLastOnline(n))
        }
    }

    @Test
    fun friendGetLastOnline_unknownFriend_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxFriendGetLastOnlineException> {
                    tox.friendGetLastOnline(ToxFriendNumber(999))
                }
            assertEquals(ToxFriendGetLastOnlineException.Code.FRIEND_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun deleteFriend_unknown_throws() {
        ToxCoreImpl(options).use { tox ->
            assertFailsWith<ToxFriendDeleteException> {
                tox.friendDelete(ToxFriendNumber(999))
            }
        }
    }

    @Test
    fun addFriendNorequest_ownKey_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxFriendAddException> {
                    tox.friendAddNorequest(tox.publicKey)
                }
            assertEquals(ToxFriendAddException.Code.OWN_KEY, ex.code)
        }
    }

    @Test
    fun addFriend_ownAddress_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxFriendAddException> {
                    tox.friendAdd(tox.address, "hi".encodeToByteArray())
                }
            assertEquals(ToxFriendAddException.Code.OWN_KEY, ex.code)
        }
    }

    @Test
    fun addFriend_emptyMessage_throws() {
        ToxCoreImpl(options).use { tox ->
            ToxCoreImpl(options).use { peer ->
                val ex =
                    assertFailsWith<ToxFriendAddException> {
                        tox.friendAdd(peer.address, ByteArray(0))
                    }
                assertEquals(ToxFriendAddException.Code.NO_MESSAGE, ex.code)
            }
        }
    }

    @Test
    fun addFriend_tooLongMessage_throws() {
        withPeerKey { _, _ ->
            ToxCoreImpl(options).use { tox ->
                ToxCoreImpl(options).use { peer ->
                    val tooLong = ByteArray(ToxCoreConstants.MAX_FRIEND_REQUEST_LENGTH + 1)
                    val ex =
                        assertFailsWith<ToxFriendAddException> {
                            tox.friendAdd(peer.address, tooLong)
                        }
                    assertEquals(ToxFriendAddException.Code.TOO_LONG, ex.code)
                }
            }
        }
    }

    @Test
    fun addFriend_wrongAddressLength_throwsIllegalArgument() {
        // Checked in Kotlin, before JNI.
        ToxCoreImpl(options).use { tox ->
            assertFailsWith<IllegalArgumentException> {
                tox.friendAdd(ToxAddress(ByteArray(10)), "hi".encodeToByteArray())
            }
        }
    }

    @Test
    fun addFriendNorequest_wrongKeyLength_throwsIllegalArgument() {
        ToxCoreImpl(options).use { tox ->
            assertFailsWith<IllegalArgumentException> {
                tox.friendAddNorequest(ToxPublicKey(ByteArray(10)))
            }
        }
    }

    @Test
    fun addFriendNorequest_two_assignsSequentialNumbers() {
        ToxCoreImpl(options).use { peer1 ->
            ToxCoreImpl(options).use { peer2 ->
                ToxCoreImpl(options).use { tox ->
                    val n1 = tox.friendAddNorequest(peer1.publicKey)
                    val n2 = tox.friendAddNorequest(peer2.publicKey)
                    assertEquals(0, n1.value)
                    assertEquals(1, n2.value)
                    assertEquals(listOf(n1, n2), tox.friendList)
                }
            }
        }
    }
}
