package im.tox.tox4j.impl.jni

import im.tox.tox4j.crypto.exceptions.ToxDecryptionException
import im.tox.tox4j.crypto.exceptions.ToxEncryptionException
import im.tox.tox4j.crypto.exceptions.ToxGetSaltException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** Java callers can pass null to the JNI shims, which Kotlin can't. */
class ToxCryptoJniNullGuardTest {
    private val passphrase = "test-passphrase".encodeToByteArray()

    @Test
    fun passKeyEncrypt_nullData_throws() {
        val pk = ToxCryptoJni.toxPassKeyDerive(passphrase)
        try {
            val ex =
                assertFailsWith<ToxEncryptionException> {
                    ToxCryptoJni.toxPassKeyEncrypt(pk, null)
                }
            assertEquals(ToxEncryptionException.Code.NULL, ex.code)
        } finally {
            ToxCryptoJni.toxPassKeyFree(pk)
        }
    }

    @Test
    fun passKeyDecrypt_nullData_throws() {
        val pk = ToxCryptoJni.toxPassKeyDerive(passphrase)
        try {
            // A null array arrives as empty, and the length check comes
            // first.
            val ex =
                assertFailsWith<ToxDecryptionException> {
                    ToxCryptoJni.toxPassKeyDecrypt(pk, null)
                }
            assertEquals(ToxDecryptionException.Code.INVALID_LENGTH, ex.code)
        } finally {
            ToxCryptoJni.toxPassKeyFree(pk)
        }
    }

    @Test
    fun getSalt_nullData_throws() {
        val ex =
            assertFailsWith<ToxGetSaltException> {
                ToxCryptoJni.toxGetSalt(null)
            }
        assertEquals(ToxGetSaltException.Code.BAD_FORMAT, ex.code)
    }
}
