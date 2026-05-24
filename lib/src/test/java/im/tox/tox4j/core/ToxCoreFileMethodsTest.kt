package im.tox.tox4j.core

import im.tox.tox4j.core.data.ToxFileNumber
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.exceptions.ToxFileGetException
import im.tox.tox4j.core.exceptions.ToxFileSeekException
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxCoreImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ToxCoreFileMethodsTest {
    private val options = ToxOptions(localDiscoveryEnabled = false)

    private inline fun withFriend(block: (ToxCoreImpl, ToxFriendNumber) -> Unit) {
        ToxCoreImpl(options).use { peer ->
            ToxCoreImpl(options).use { tox ->
                block(tox, tox.friendAddNorequest(peer.publicKey))
            }
        }
    }

    @Test
    fun fileSeek_unknownFriend_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxFileSeekException> {
                    tox.fileSeek(ToxFriendNumber(999), ToxFileNumber(0), 0L)
                }
            assertEquals(ToxFileSeekException.Code.FRIEND_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun fileSeek_unknownFile_throws() {
        withFriend { tox, friend ->
            val ex =
                assertFailsWith<ToxFileSeekException> {
                    tox.fileSeek(friend, ToxFileNumber(999), 0L)
                }
            // The connection is checked before the file number.
            assertEquals(ToxFileSeekException.Code.FRIEND_NOT_CONNECTED, ex.code)
        }
    }

    @Test
    fun fileGetFileId_unknownFriend_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxFileGetException> {
                    tox.fileGetFileId(ToxFriendNumber(999), ToxFileNumber(0))
                }
            assertEquals(ToxFileGetException.Code.FRIEND_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun fileGetFileId_unknownFile_throws() {
        withFriend { tox, friend ->
            val ex =
                assertFailsWith<ToxFileGetException> {
                    tox.fileGetFileId(friend, ToxFileNumber(999))
                }
            assertEquals(ToxFileGetException.Code.NOT_FOUND, ex.code)
        }
    }
}
