package im.tox.tox4j.core.options

import im.tox.tox4j.core.ToxCoreConstants
import im.tox.tox4j.core.data.ToxPort
import im.tox.tox4j.core.data.ToxSecretKey
import im.tox.tox4j.core.enums.ToxProxyType
import im.tox.tox4j.core.enums.ToxSavedataType
import im.tox.tox4j.impl.jni.ToxCoreImpl
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ToxOptionsTest {
    private val baseOptions = ToxOptions(localDiscoveryEnabled = false)

    @Test
    fun defaults_areConservative() {
        val defaults = ToxOptions()
        assertEquals(true, defaults.ipv6Enabled)
        assertEquals(true, defaults.udpEnabled)
        assertEquals(true, defaults.localDiscoveryEnabled)
        assertEquals(ToxPort(ToxCoreConstants.DEFAULT_START_PORT), defaults.startPort)
        assertEquals(ToxPort(ToxCoreConstants.DEFAULT_END_PORT), defaults.endPort)
        assertEquals(ToxPort(ToxCoreConstants.DEFAULT_TCP_PORT), defaults.tcpPort)
        assertTrue(defaults.proxy is ProxyOptions.None)
        assertTrue(defaults.savedata is SavedataOptions.None)
    }

    @Test
    fun proxyNone_hasNoneEnum() {
        assertEquals(ToxProxyType.NONE, ProxyOptions.None.proxyType)
    }

    @Test
    fun proxyHttp_carriesHostPort() {
        val proxy = ProxyOptions.Http("proxy.example.com", ToxPort(8080))
        assertEquals(ToxProxyType.HTTP, proxy.proxyType)
        assertEquals("proxy.example.com", proxy.proxyHost)
        assertEquals(ToxPort(8080), proxy.proxyPort)
    }

    @Test
    fun proxySocks5_carriesHostPort() {
        val proxy = ProxyOptions.Socks5("127.0.0.1", ToxPort(9050))
        assertEquals(ToxProxyType.SOCKS5, proxy.proxyType)
        assertEquals("127.0.0.1", proxy.proxyHost)
        assertEquals(ToxPort(9050), proxy.proxyPort)
    }

    @Test
    fun savedata_none_isEmpty() {
        assertEquals(ToxSavedataType.NONE, SavedataOptions.None.savedataType)
        assertEquals(0, SavedataOptions.None.savedata.size)
    }

    @Test
    fun savedata_toxSave_exposesData() {
        val raw = byteArrayOf(1, 2, 3, 4)
        val save = SavedataOptions.ToxSave(raw)
        assertEquals(ToxSavedataType.TOX_SAVE, save.savedataType)
        assertContentEquals(raw, save.savedata)
    }

    @Test
    fun savedata_secretKey_exposesData() {
        val key = ToxSecretKey(ByteArray(ToxCoreConstants.SECRET_KEY_SIZE) { 7 })
        val save = SavedataOptions.SecretKey(key)
        assertEquals(ToxSavedataType.SECRET_KEY, save.savedataType)
        assertContentEquals(key.value, save.savedata)
    }

    @Test
    fun construct_withProxyNone_ok() = ToxCoreImpl(baseOptions.copy(proxy = ProxyOptions.None)).use { }

    @Test
    fun construct_withSavedataNone_ok() = ToxCoreImpl(baseOptions.copy(savedata = SavedataOptions.None)).use { }

    @Test
    fun construct_withIpv4Only_ok() = ToxCoreImpl(baseOptions.copy(ipv6Enabled = false)).use { }

    @Test
    fun construct_withTcpOnly_ok() = ToxCoreImpl(baseOptions.copy(udpEnabled = false)).use { }

    @Test
    fun construct_withTcpServerEnabled_ok() = ToxCoreImpl(baseOptions.copy(tcpPort = ToxPort(0))).use { }

    @Test
    fun construct_withSinglePortRange_ok() = ToxCoreImpl(baseOptions.copy(startPort = ToxPort(40000), endPort = ToxPort(40000))).use { }

    @Test
    fun construct_withWidePortRange_ok() = ToxCoreImpl(baseOptions.copy(startPort = ToxPort(33445), endPort = ToxPort(33545))).use { }

    @Test
    fun construct_withToxSavedataRoundTrip_ok() {
        val data: ByteArray
        ToxCoreImpl(baseOptions).use { tox -> data = tox.savedata }
        ToxCoreImpl(baseOptions.copy(savedata = SavedataOptions.ToxSave(data))).use { }
    }

    @Test
    fun construct_withSecretKeyData_ok() {
        val sk: ToxSecretKey
        ToxCoreImpl(baseOptions).use { tox -> sk = tox.secretKey }
        ToxCoreImpl(baseOptions.copy(savedata = SavedataOptions.SecretKey(sk))).use { }
    }

    @Test
    fun copy_isStructural() {
        val a = ToxOptions(ipv6Enabled = false, udpEnabled = true)
        val b = a.copy(ipv6Enabled = true)
        assertEquals(false, a.ipv6Enabled)
        assertEquals(true, b.ipv6Enabled)
        assertEquals(a.udpEnabled, b.udpEnabled)
    }

    @Test
    fun toxCoreImpl_dropsSavedataAfterInit() {
        // The C side keeps its own copy, so the options don't hold on to the bytes.
        val data: ByteArray
        ToxCoreImpl(baseOptions).use { tox -> data = tox.savedata }
        val withSavedata = baseOptions.copy(savedata = SavedataOptions.ToxSave(data))
        ToxCoreImpl(withSavedata).use { tox ->
            assertEquals(SavedataOptions.None, tox.options.savedata)
            assertEquals(0, tox.options.savedata.savedata.size)
        }
    }

    @Test
    fun toxSave_equals_isContentBased() {
        val bytes1 = byteArrayOf(1, 2, 3, 4, 5)
        val bytes2 = byteArrayOf(1, 2, 3, 4, 5)
        assertEquals(SavedataOptions.ToxSave(bytes1), SavedataOptions.ToxSave(bytes2))
        assertEquals(
            SavedataOptions.ToxSave(bytes1).hashCode(),
            SavedataOptions.ToxSave(bytes2).hashCode(),
        )
    }

    @Test
    fun toxSave_equals_distinguishesContent() {
        val a = SavedataOptions.ToxSave(byteArrayOf(1, 2, 3))
        val b = SavedataOptions.ToxSave(byteArrayOf(1, 2, 4))
        assertTrue(a != b)
    }

    @Test
    fun toxSave_toString_omitsBytes() {
        // Save data holds the secret key.
        assertEquals("ToxSave(savedata=<5 bytes>)", SavedataOptions.ToxSave(ByteArray(5)).toString())
    }

    @Test
    fun secretKey_equals_isContentBased() {
        val k1 = ToxSecretKey(ByteArray(ToxCoreConstants.SECRET_KEY_SIZE) { 1 })
        val k2 = ToxSecretKey(ByteArray(ToxCoreConstants.SECRET_KEY_SIZE) { 1 })
        assertEquals(SavedataOptions.SecretKey(k1), SavedataOptions.SecretKey(k2))
    }

    @Test
    fun secretKey_toString_omitsBytes() {
        val k = ToxSecretKey(ByteArray(ToxCoreConstants.SECRET_KEY_SIZE))
        assertEquals(
            "SecretKey(<${ToxCoreConstants.SECRET_KEY_SIZE} bytes>)",
            SavedataOptions.SecretKey(k).toString(),
        )
    }
}
