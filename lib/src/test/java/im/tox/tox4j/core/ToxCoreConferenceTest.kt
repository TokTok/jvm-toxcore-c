package im.tox.tox4j.core

import im.tox.tox4j.core.data.ToxConferenceId
import im.tox.tox4j.core.data.ToxConferenceNumber
import im.tox.tox4j.core.enums.ToxConferenceType
import im.tox.tox4j.core.exceptions.ToxConferenceByIdException
import im.tox.tox4j.core.exceptions.ToxConferenceDeleteException
import im.tox.tox4j.core.exceptions.ToxConferenceGetTypeException
import im.tox.tox4j.core.exceptions.ToxConferenceTitleException
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxCoreImpl
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(kotlin.ExperimentalStdlibApi::class)
class ToxCoreConferenceTest {
    private val options = ToxOptions(localDiscoveryEnabled = false)

    @Test
    fun new_returnsZeroOnFirst() {
        ToxCoreImpl(options).use { tox ->
            assertEquals(0, tox.conferenceNew().value)
        }
    }

    @Test
    fun new_thenDelete_succeeds() {
        ToxCoreImpl(options).use { tox ->
            val conf = tox.conferenceNew()
            tox.conferenceDelete(conf)
        }
    }

    @Test
    fun chatlist_emptyInitially() {
        ToxCoreImpl(options).use { tox ->
            assertEquals(0, tox.conferenceChatlist.size)
        }
    }

    @Test
    fun chatlist_reflectsActiveConferences() {
        ToxCoreImpl(options).use { tox ->
            val a = tox.conferenceNew()
            val b = tox.conferenceNew()
            assertEquals(
                setOf(a, b),
                tox.conferenceChatlist.toSet(),
            )
            tox.conferenceDelete(a)
            assertEquals(listOf(b), tox.conferenceChatlist)
        }
    }

    @Test
    fun newTwo_distinctNumbers() {
        ToxCoreImpl(options).use { tox ->
            val a = tox.conferenceNew()
            val b = tox.conferenceNew()
            assertEquals(0, a.value)
            assertEquals(1, b.value)
        }
    }

    @Test
    fun reuseNumber_afterDelete() {
        ToxCoreImpl(options).use { tox ->
            val a = tox.conferenceNew()
            tox.conferenceDelete(a)
            val b = tox.conferenceNew()
            assertEquals(0, b.value)
        }
    }

    // On these errors the C functions return out-of-range enum values, which UBSan
    // traps if they get converted.

    @Test
    fun getType_unknownConference_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxConferenceGetTypeException> {
                    tox.conferenceGetType(ToxConferenceNumber(999))
                }
            assertEquals(ToxConferenceGetTypeException.Code.CONFERENCE_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun getTitle_beforeSet_throwsInvalidLength() {
        // c-toxcore reports a missing title as INVALID_LENGTH.
        ToxCoreImpl(options).use { tox ->
            val conf = tox.conferenceNew()
            val ex =
                assertFailsWith<ToxConferenceTitleException> {
                    tox.conferenceGetTitle(conf)
                }
            assertEquals(ToxConferenceTitleException.Code.INVALID_LENGTH, ex.code)
        }
    }

    @Test
    fun setTitle_thenGetTitle_roundtrip() {
        ToxCoreImpl(options).use { tox ->
            val conf = tox.conferenceNew()
            val title = "My Chat".encodeToByteArray()
            tox.conferenceSetTitle(conf, title)
            assertContentEquals(title, tox.conferenceGetTitle(conf))
        }
    }

    @Test
    fun setTitle_unknownConference_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxConferenceTitleException> {
                    tox.conferenceSetTitle(
                        ToxConferenceNumber(999),
                        "x".encodeToByteArray(),
                    )
                }
            assertEquals(ToxConferenceTitleException.Code.CONFERENCE_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun delete_unknown_throws() {
        ToxCoreImpl(options).use { tox ->
            val ex =
                assertFailsWith<ToxConferenceDeleteException> {
                    tox.conferenceDelete(ToxConferenceNumber(999))
                }
            assertEquals(ToxConferenceDeleteException.Code.CONFERENCE_NOT_FOUND, ex.code)
        }
    }

    @Test
    fun getId_returnsExpectedLength() {
        ToxCoreImpl(options).use { tox ->
            val conf = tox.conferenceNew()
            assertEquals(32, tox.conferenceGetId(conf).value.size)
        }
    }

    @Test
    fun conferenceById_unknown_throws() {
        ToxCoreImpl(options).use { tox ->
            val bogus = ToxConferenceId(ByteArray(32))
            val ex =
                assertFailsWith<ToxConferenceByIdException> {
                    tox.conferenceById(bogus)
                }
            assertEquals(ToxConferenceByIdException.Code.NOT_FOUND, ex.code)
        }
    }

    @Test
    fun getType_defaultIsText() {
        ToxCoreImpl(options).use { tox ->
            val conf = tox.conferenceNew()
            assertEquals(ToxConferenceType.TEXT, tox.conferenceGetType(conf))
        }
    }
}
