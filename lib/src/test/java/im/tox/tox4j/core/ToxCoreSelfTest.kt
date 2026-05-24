package im.tox.tox4j.core

import im.tox.tox4j.core.enums.ToxUserStatus
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxCoreImpl
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@OptIn(kotlin.ExperimentalStdlibApi::class)
class ToxCoreSelfTest {
    private val options = ToxOptions(localDiscoveryEnabled = false)

    @Test
    fun nospam_roundtrip() {
        ToxCoreImpl(options).use { tox ->
            tox.setNospam(0x12345678)
            assertEquals(0x12345678, tox.nospam)
        }
    }

    @Test
    fun nospam_zero_roundtrip() {
        ToxCoreImpl(options).use { tox ->
            tox.setNospam(0)
            assertEquals(0, tox.nospam)
        }
    }

    @Test
    fun nospam_negative_roundtrip() {
        // The uint32 nospam comes back as a two's-complement Int.
        ToxCoreImpl(options).use { tox ->
            tox.setNospam(-1)
            assertEquals(-1, tox.nospam)
        }
    }

    @Test
    fun name_short_roundtrip() {
        ToxCoreImpl(options).use { tox ->
            val name = "Alice".encodeToByteArray()
            tox.setName(name)
            assertContentEquals(name, tox.name)
        }
    }

    @Test
    fun name_empty_roundtrip() {
        ToxCoreImpl(options).use { tox ->
            tox.setName(ByteArray(0))
            assertContentEquals(ByteArray(0), tox.name)
        }
    }

    @Test
    fun name_max_length_roundtrip() {
        ToxCoreImpl(options).use { tox ->
            val name = ByteArray(ToxCoreConstants.MAX_NAME_LENGTH) { 'a'.code.toByte() }
            tox.setName(name)
            assertContentEquals(name, tox.name)
        }
    }

    @Test
    fun name_nonAscii_roundtrip() {
        ToxCoreImpl(options).use { tox ->
            val name = "Алисa🐍".encodeToByteArray()
            tox.setName(name)
            assertContentEquals(name, tox.name)
        }
    }

    @Test
    fun statusMessage_short_roundtrip() {
        ToxCoreImpl(options).use { tox ->
            val msg = "online".encodeToByteArray()
            tox.setStatusMessage(msg)
            assertContentEquals(msg, tox.statusMessage)
        }
    }

    @Test
    fun statusMessage_empty_roundtrip() {
        ToxCoreImpl(options).use { tox ->
            tox.setStatusMessage(ByteArray(0))
            assertContentEquals(ByteArray(0), tox.statusMessage)
        }
    }

    @Test
    fun statusMessage_max_length_roundtrip() {
        ToxCoreImpl(options).use { tox ->
            val msg = ByteArray(ToxCoreConstants.MAX_STATUS_MESSAGE_LENGTH) { 'x'.code.toByte() }
            tox.setStatusMessage(msg)
            assertContentEquals(msg, tox.statusMessage)
        }
    }

    @Test
    fun status_none_roundtrip() = statusRoundtripOf(ToxUserStatus.NONE)

    @Test
    fun status_away_roundtrip() = statusRoundtripOf(ToxUserStatus.AWAY)

    @Test
    fun status_busy_roundtrip() = statusRoundtripOf(ToxUserStatus.BUSY)

    private fun statusRoundtripOf(status: ToxUserStatus) {
        ToxCoreImpl(options).use { tox ->
            tox.setStatus(status)
            assertEquals(status, tox.status)
        }
    }

    @Test
    fun status_default_isNone() {
        ToxCoreImpl(options).use { tox ->
            assertEquals(ToxUserStatus.NONE, tox.status)
        }
    }

    @Test
    fun name_default_isEmpty() {
        ToxCoreImpl(options).use { tox ->
            assertEquals(0, tox.name.size)
        }
    }

    @Test
    fun statusMessage_default_isEmpty() {
        ToxCoreImpl(options).use { tox ->
            assertEquals(0, tox.statusMessage.size)
        }
    }

    @Test
    fun setNospam_modifiesAddress() {
        ToxCoreImpl(options).use { tox ->
            tox.setNospam(0x11111111)
            val addrA = tox.address.value.copyOf()
            tox.setNospam(0x22222222)
            val addrB = tox.address.value
            assertContentEquals(
                addrA.copyOfRange(0, ToxCoreConstants.PUBLIC_KEY_SIZE),
                addrB.copyOfRange(0, ToxCoreConstants.PUBLIC_KEY_SIZE),
            )
            // Single bytes can match by chance, the whole tail can't.
            val tailA = addrA.copyOfRange(ToxCoreConstants.PUBLIC_KEY_SIZE, ToxCoreConstants.ADDRESS_SIZE)
            val tailB = addrB.copyOfRange(ToxCoreConstants.PUBLIC_KEY_SIZE, ToxCoreConstants.ADDRESS_SIZE)
            kotlin.test.assertFalse(tailA.contentEquals(tailB))
        }
    }
}
