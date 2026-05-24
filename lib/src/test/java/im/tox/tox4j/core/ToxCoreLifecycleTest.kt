package im.tox.tox4j.core

import im.tox.tox4j.core.data.ToxDhtId
import im.tox.tox4j.core.data.ToxPort
import im.tox.tox4j.core.data.ToxPublicKey
import im.tox.tox4j.core.data.ToxSecretKey
import im.tox.tox4j.core.options.ProxyOptions
import im.tox.tox4j.core.options.SavedataOptions
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.exceptions.ToxKilledException
import im.tox.tox4j.impl.jni.ToxCoreImpl
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ToxCoreLifecycleTest {
    private val options = ToxOptions(localDiscoveryEnabled = false)

    @Test
    fun construct_withDefaults_succeeds() = ToxCoreImpl(options).use { }

    @Test
    fun construct_withIpv6Disabled_succeeds() = ToxCoreImpl(options.copy(ipv6Enabled = false)).use { }

    @Test
    fun construct_withUdpDisabled_succeeds() = ToxCoreImpl(options.copy(udpEnabled = false)).use { }

    @Test
    fun construct_withLocalDiscoveryEnabled_succeeds() = ToxCoreImpl(options.copy(localDiscoveryEnabled = true)).use { }

    @Test
    fun construct_withProxyNone_succeeds() = ToxCoreImpl(options.copy(proxy = ProxyOptions.None)).use { }

    @Test
    fun construct_withTcpServerDisabled_succeeds() = ToxCoreImpl(options.copy(tcpPort = ToxPort(0))).use { }

    @Test
    fun construct_withCustomPortRange_succeeds() = ToxCoreImpl(options.copy(startPort = ToxPort(40000), endPort = ToxPort(40100))).use { }

    @Test
    fun construct_withEmptySavedata_succeeds() = ToxCoreImpl(options.copy(savedata = SavedataOptions.None)).use { }

    @Test
    fun close_thenAnyCall_throwsToxKilled() {
        val tox = ToxCoreImpl(options)
        tox.close()
        assertFailsWith<ToxKilledException> { tox.publicKey }
    }

    @Test
    fun use_closesAutomatically() {
        val tox = ToxCoreImpl(options)
        tox.use { /* no-op */ }
        assertFailsWith<ToxKilledException> { tox.publicKey }
    }

    @Test
    fun finalize_afterClose_recoversInstanceSlot() {
        // Passes if finalize doesn't throw.
        val tox = ToxCoreImpl(options)
        tox.close()
        ToxCoreImpl::class.java
            .getDeclaredMethod("finalize")
            .apply { isAccessible = true }
            .invoke(tox)
    }

    @Test
    fun finalize_withoutClose_doesNotThrow() {
        // Never closed: finalize kills the instance, then frees its slot.
        val tox = ToxCoreImpl(options)
        ToxCoreImpl::class.java
            .getDeclaredMethod("finalize")
            .apply { isAccessible = true }
            .invoke(tox)
        // A freed slot throws IllegalStateException, a closed one ToxKilledException.
        assertFailsWith<IllegalStateException> { tox.publicKey }
    }

    @Test
    fun load_fromSavedata_preservesIdentity() {
        val savedata: ByteArray
        val originalPublicKey: ToxPublicKey
        val originalAddress: ByteArray
        ToxCoreImpl(options).use { tox ->
            savedata = tox.savedata
            originalPublicKey = tox.publicKey
            originalAddress = tox.address.value
        }
        assertTrue(savedata.isNotEmpty())

        val reloaded = options.copy(savedata = SavedataOptions.ToxSave(savedata))
        ToxCoreImpl(reloaded).use { tox ->
            assertContentEquals(originalPublicKey.value, tox.publicKey.value)
            assertContentEquals(originalAddress, tox.address.value)
        }
    }

    @Test
    fun secretKey_savedataLoad_preservesPublicKey() {
        val secretKey: ToxSecretKey
        val originalPublicKey: ToxPublicKey
        ToxCoreImpl(options).use { tox ->
            secretKey = tox.secretKey
            originalPublicKey = tox.publicKey
        }
        val reloaded = options.copy(savedata = SavedataOptions.SecretKey(secretKey))
        ToxCoreImpl(reloaded).use { tox ->
            assertContentEquals(originalPublicKey.value, tox.publicKey.value)
        }
    }

    @Test
    fun iterationInterval_isPositive() = ToxCoreImpl(options).use { tox -> assertTrue(tox.iterationInterval > 0) }

    @Test
    fun toxDhtId_withShortKey_throwsIllegalArgument() {
        assertFailsWith<IllegalArgumentException> {
            ToxDhtId(ByteArray(16))
        }
    }

    @Test
    fun toxDhtId_withLongKey_throwsIllegalArgument() {
        assertFailsWith<IllegalArgumentException> {
            ToxDhtId(ByteArray(64))
        }
    }

    @Test
    fun savedata_isNonTrivial() {
        ToxCoreImpl(options).use { tox ->
            val savedata = tox.savedata
            // More than just the keys, so not a stub.
            assertTrue(savedata.size > ToxCoreConstants.SECRET_KEY_SIZE + ToxCoreConstants.PUBLIC_KEY_SIZE)
        }
    }

    @Test
    fun friendList_isEmptyInitially() {
        ToxCoreImpl(options).use { tox ->
            assertEquals(0, tox.friendList.size)
        }
    }

    @Test
    fun dhtId_hasPublicKeySize() {
        ToxCoreImpl(options).use { tox ->
            assertEquals(ToxCoreConstants.PUBLIC_KEY_SIZE, tox.dhtId.value.size)
        }
    }

    @Test
    fun publicKey_hasExpectedSize() {
        ToxCoreImpl(options).use { tox ->
            assertEquals(ToxCoreConstants.PUBLIC_KEY_SIZE, tox.publicKey.value.size)
        }
    }

    @Test
    fun secretKey_hasExpectedSize() {
        ToxCoreImpl(options).use { tox ->
            assertEquals(ToxCoreConstants.SECRET_KEY_SIZE, tox.secretKey.value.size)
        }
    }

    @Test
    fun address_hasExpectedSize() {
        ToxCoreImpl(options).use { tox ->
            assertEquals(ToxCoreConstants.ADDRESS_SIZE, tox.address.value.size)
        }
    }

    @Test
    fun address_startsWithPublicKey() {
        ToxCoreImpl(options).use { tox ->
            val publicKey = tox.publicKey.value
            val address = tox.address.value
            assertContentEquals(publicKey, address.copyOfRange(0, ToxCoreConstants.PUBLIC_KEY_SIZE))
        }
    }

    @Test
    fun udpPort_isPositive() {
        ToxCoreImpl(options).use { tox ->
            assertTrue(tox.udpPort.value > 0)
        }
    }

    @Test
    fun publicKey_isDifferentBetweenInstances() {
        ToxCoreImpl(options).use { a ->
            ToxCoreImpl(options).use { b ->
                assertNotNull(a.publicKey)
                assertNotNull(b.publicKey)
                assertTrue(!a.publicKey.value.contentEquals(b.publicKey.value))
            }
        }
    }
}
