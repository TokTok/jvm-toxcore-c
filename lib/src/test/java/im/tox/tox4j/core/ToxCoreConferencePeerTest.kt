package im.tox.tox4j.core

import im.tox.tox4j.core.data.ToxConferenceNumber
import im.tox.tox4j.core.data.ToxConferenceOfflinePeerNumber
import im.tox.tox4j.core.data.ToxConferencePeerNumber
import im.tox.tox4j.core.exceptions.ToxConferencePeerQueryException
import im.tox.tox4j.core.exceptions.ToxConferenceSetMaxOfflineException
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxCoreImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ToxCoreConferencePeerTest {
    private val options = ToxOptions(localDiscoveryEnabled = false)

    @Test
    fun peerCount_freshConference_isOne() {
        ToxCoreImpl(options).use { tox ->
            val conf = tox.conferenceNew()
            assertEquals(1, tox.conferencePeerCount(conf))
        }
    }

    @Test
    fun peerCount_unknownConference_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxConferencePeerQueryException> {
                    tox.conferencePeerCount(ToxConferenceNumber(999))
                }
            assertEquals(ToxConferencePeerQueryException.Code.CONFERENCE_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun offlinePeerCount_freshConference_isZero() {
        ToxCoreImpl(options).use { tox ->
            val conf = tox.conferenceNew()
            assertEquals(0, tox.conferenceOfflinePeerCount(conf))
        }
    }

    @Test
    fun offlinePeerCount_unknownConference_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxConferencePeerQueryException> {
                    tox.conferenceOfflinePeerCount(ToxConferenceNumber(999))
                }
            assertEquals(ToxConferencePeerQueryException.Code.CONFERENCE_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun setMaxOffline_unknownConference_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxConferenceSetMaxOfflineException> {
                    tox.conferenceSetMaxOffline(ToxConferenceNumber(999), 10)
                }
            assertEquals(ToxConferenceSetMaxOfflineException.Code.CONFERENCE_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun setMaxOffline_validConference_succeeds() {
        ToxCoreImpl(options).use { tox ->
            val conf = tox.conferenceNew()
            tox.conferenceSetMaxOffline(conf, 50)
        }
    }

    @Test
    fun peerGetName_selfPeer_returnsEmptyBeforeSet() {
        ToxCoreImpl(options).use { tox ->
            val conf = tox.conferenceNew()
            // Peer 0 is us, with no name set.
            val name = tox.conferencePeerGetName(conf, ToxConferencePeerNumber(0))
            assertEquals(0, name.size)
        }
    }

    @Test
    fun peerGetName_unknownPeer_throws() {
        ToxCoreImpl(options).use { tox ->
            val conf = tox.conferenceNew()
            val ex =
                assertFailsWith<ToxConferencePeerQueryException> {
                    tox.conferencePeerGetName(conf, ToxConferencePeerNumber(999))
                }
            assertEquals(ToxConferencePeerQueryException.Code.PEER_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun peerGetPublicKey_selfPeer_returnsKey() {
        ToxCoreImpl(options).use { tox ->
            val conf = tox.conferenceNew()
            val key = tox.conferencePeerGetPublicKey(conf, ToxConferencePeerNumber(0))
            assertEquals(ToxCoreConstants.PUBLIC_KEY_SIZE, key.value.size)
        }
    }

    @Test
    fun peerGetPublicKey_unknownPeer_throws() {
        ToxCoreImpl(options).use { tox ->
            val conf = tox.conferenceNew()
            val ex =
                assertFailsWith<ToxConferencePeerQueryException> {
                    tox.conferencePeerGetPublicKey(conf, ToxConferencePeerNumber(999))
                }
            assertEquals(ToxConferencePeerQueryException.Code.PEER_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun peerNumberIsOurs_selfPeer_isTrue() {
        ToxCoreImpl(options).use { tox ->
            val conf = tox.conferenceNew()
            assertTrue(tox.conferencePeerNumberIsOurs(conf, ToxConferencePeerNumber(0)))
        }
    }

    @Test
    fun peerNumberIsOurs_unknownConference_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxConferencePeerQueryException> {
                    tox.conferencePeerNumberIsOurs(ToxConferenceNumber(999), ToxConferencePeerNumber(0))
                }
            assertEquals(ToxConferencePeerQueryException.Code.CONFERENCE_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun offlinePeerGetName_unknownPeer_throws() {
        ToxCoreImpl(options).use { tox ->
            val conf = tox.conferenceNew()
            val ex =
                assertFailsWith<ToxConferencePeerQueryException> {
                    tox.conferenceOfflinePeerGetName(conf, ToxConferenceOfflinePeerNumber(0))
                }
            assertEquals(ToxConferencePeerQueryException.Code.PEER_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun offlinePeerGetPublicKey_unknownConference_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxConferencePeerQueryException> {
                    tox.conferenceOfflinePeerGetPublicKey(
                        ToxConferenceNumber(999),
                        ToxConferenceOfflinePeerNumber(0),
                    )
                }
            assertEquals(ToxConferencePeerQueryException.Code.CONFERENCE_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun offlinePeerGetLastActive_unknownPeer_throws() {
        ToxCoreImpl(options).use { tox ->
            val conf = tox.conferenceNew()
            val ex =
                assertFailsWith<ToxConferencePeerQueryException> {
                    tox.conferenceOfflinePeerGetLastActive(conf, ToxConferenceOfflinePeerNumber(0))
                }
            assertEquals(ToxConferencePeerQueryException.Code.PEER_NOT_FOUND, ex.code)
        }
    }
}
