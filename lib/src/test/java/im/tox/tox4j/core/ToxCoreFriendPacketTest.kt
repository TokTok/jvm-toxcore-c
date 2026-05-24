package im.tox.tox4j.core

import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.data.ToxPublicKey
import im.tox.tox4j.core.exceptions.ToxFriendCustomPacketException
import im.tox.tox4j.core.options.SavedataOptions
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxCoreImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** The friend isn't connected, so a valid packet fails with FRIEND_NOT_CONNECTED. */
class ToxCoreFriendPacketTest {
    private val options = ToxOptions(localDiscoveryEnabled = false)

    private inline fun withFriend(block: (ToxCoreImpl, ToxFriendNumber) -> Unit) {
        ToxCoreImpl(options).use { peer ->
            ToxCoreImpl(options).use { tox ->
                val friend = tox.friendAddNorequest(peer.publicKey)
                block(tox, friend)
            }
        }
    }

    @Test
    fun lossyPacket_badFirstByte_throwsInvalid() {
        withFriend { tox, friend ->
            val ex =
                assertFailsWith<ToxFriendCustomPacketException> {
                    tox.friendSendLossyPacket(friend, byteArrayOf(100, 1, 2, 3))
                }
            assertEquals(ToxFriendCustomPacketException.Code.INVALID, ex.code)
        }
    }

    @Test
    fun lossyPacket_empty_throwsEmpty() {
        withFriend { tox, friend ->
            val ex =
                assertFailsWith<ToxFriendCustomPacketException> {
                    tox.friendSendLossyPacket(friend, ByteArray(0))
                }
            assertEquals(ToxFriendCustomPacketException.Code.EMPTY, ex.code)
        }
    }

    @Test
    fun lossyPacket_tooLong_throwsTooLong() {
        withFriend { tox, friend ->
            val data = ByteArray(ToxCoreConstants.MAX_CUSTOM_PACKET_SIZE + 1)
            data[0] = 200.toByte()
            val ex =
                assertFailsWith<ToxFriendCustomPacketException> {
                    tox.friendSendLossyPacket(friend, data)
                }
            assertEquals(ToxFriendCustomPacketException.Code.TOO_LONG, ex.code)
        }
    }

    @Test
    fun lossyPacket_validRange_failsOnlyOnDisconnect() {
        withFriend { tox, friend ->
            val ex =
                assertFailsWith<ToxFriendCustomPacketException> {
                    tox.friendSendLossyPacket(friend, byteArrayOf(200.toByte(), 1, 2))
                }
            assertEquals(ToxFriendCustomPacketException.Code.FRIEND_NOT_CONNECTED, ex.code)
        }
    }

    @Test
    fun losslessPacket_badFirstByte_throwsInvalid() {
        withFriend { tox, friend ->
            val ex =
                assertFailsWith<ToxFriendCustomPacketException> {
                    tox.friendSendLosslessPacket(friend, byteArrayOf(100, 1, 2))
                }
            assertEquals(ToxFriendCustomPacketException.Code.INVALID, ex.code)
        }
    }

    @Test
    fun losslessPacket_empty_throwsEmpty() {
        withFriend { tox, friend ->
            val ex =
                assertFailsWith<ToxFriendCustomPacketException> {
                    tox.friendSendLosslessPacket(friend, ByteArray(0))
                }
            assertEquals(ToxFriendCustomPacketException.Code.EMPTY, ex.code)
        }
    }

    @Test
    fun losslessPacket_validRange_failsOnlyOnDisconnect() {
        withFriend { tox, friend ->
            val ex =
                assertFailsWith<ToxFriendCustomPacketException> {
                    tox.friendSendLosslessPacket(friend, byteArrayOf(160.toByte(), 1, 2))
                }
            assertEquals(ToxFriendCustomPacketException.Code.FRIEND_NOT_CONNECTED, ex.code)
        }
    }

    @Test
    fun friendList_threeFriends_orderPreserved() {
        ToxCoreImpl(options).use { peer1 ->
            ToxCoreImpl(options).use { peer2 ->
                ToxCoreImpl(options).use { peer3 ->
                    ToxCoreImpl(options).use { tox ->
                        val f1 = tox.friendAddNorequest(peer1.publicKey)
                        val f2 = tox.friendAddNorequest(peer2.publicKey)
                        val f3 = tox.friendAddNorequest(peer3.publicKey)
                        assertEquals(0, f1.value)
                        assertEquals(1, f2.value)
                        assertEquals(2, f3.value)
                        assertEquals(listOf(f1, f2, f3), tox.friendList)
                    }
                }
            }
        }
    }

    @Test
    fun friendList_deleteMiddle_reusesSlot() {
        // Tox reuses freed friend numbers.
        ToxCoreImpl(options).use { peer1 ->
            ToxCoreImpl(options).use { peer2 ->
                ToxCoreImpl(options).use { peer3 ->
                    ToxCoreImpl(options).use { peer4 ->
                        ToxCoreImpl(options).use { tox ->
                            tox.friendAddNorequest(peer1.publicKey)
                            val f1 = tox.friendAddNorequest(peer2.publicKey)
                            tox.friendAddNorequest(peer3.publicKey)
                            tox.friendDelete(f1)
                            val f4 = tox.friendAddNorequest(peer4.publicKey)
                            assertEquals(1, f4.value, "Slot 1 should be reused after delete")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun savedata_preservesFriends() {
        val savedata: ByteArray
        val expectedFriendKey: ByteArray
        ToxCoreImpl(options).use { peer ->
            expectedFriendKey = peer.publicKey.value
            ToxCoreImpl(options).use { tox ->
                tox.friendAddNorequest(ToxPublicKey(expectedFriendKey))
                savedata = tox.savedata
            }
        }
        ToxCoreImpl(
            options.copy(
                savedata = SavedataOptions.ToxSave(savedata),
            ),
        ).use { tox ->
            assertEquals(
                listOf(ToxFriendNumber(0)),
                tox.friendList,
            )
            kotlin.test.assertContentEquals(
                expectedFriendKey,
                tox
                    .friendGetPublicKey(
                        im.tox.tox4j.core.data
                            .ToxFriendNumber(0),
                    ).value,
            )
        }
    }
}
