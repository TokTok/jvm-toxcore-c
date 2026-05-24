package im.tox.tox4j.core.data

import im.tox.tox4j.core.ToxCoreConstants
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/** A generated fixed-size wrapper. */
class ToxByteArrayValueEqualityTest {
    private val publicKeyBytes: () -> ByteArray = { ByteArray(ToxCoreConstants.PUBLIC_KEY_SIZE) { 0xA5.toByte() } }

    @Test
    fun fixedSize_equality_isContentBased() {
        val a = ToxPublicKey(publicKeyBytes())
        val b = ToxPublicKey(publicKeyBytes())
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun fixedSize_distinguishesContent() {
        val a = ToxPublicKey(ByteArray(ToxCoreConstants.PUBLIC_KEY_SIZE) { 1 })
        val b = ToxPublicKey(ByteArray(ToxCoreConstants.PUBLIC_KEY_SIZE) { 2 })
        assertNotEquals(a, b)
    }

    @Test
    fun fixedSize_toString_omitsBytes() {
        // Not secret, but every wrapper hides its bytes so the secret ones never leak.
        assertEquals(
            "ToxPublicKey(<${ToxCoreConstants.PUBLIC_KEY_SIZE} bytes>)",
            ToxPublicKey(publicKeyBytes()).toString(),
        )
    }

    @Test
    fun fixedSize_worksAsMapKey() {
        val map = mutableMapOf<ToxPublicKey, String>()
        map[ToxPublicKey(publicKeyBytes())] = "alice"
        val lookup = map[ToxPublicKey(publicKeyBytes())]
        assertEquals("alice", lookup)
    }

    @Test
    fun reference_equality_unchanged() {
        val sample = ToxPublicKey(publicKeyBytes())
        assertTrue(sample === sample)
        assertFalse(sample === ToxPublicKey(publicKeyBytes()))
    }

    @Test
    fun differentWrapperTypes_withIdenticalBytes_areNotEqual() {
        val bytes = ByteArray(ToxCoreConstants.PUBLIC_KEY_SIZE) { 1 }
        assertNotEquals<Any>(ToxPublicKey(bytes), ToxSecretKey(bytes))
    }
}
