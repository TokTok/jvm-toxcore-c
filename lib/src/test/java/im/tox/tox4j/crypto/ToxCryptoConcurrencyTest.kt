package im.tox.tox4j.crypto

import im.tox.tox4j.crypto.exceptions.ToxEncryptionException
import im.tox.tox4j.impl.jni.ToxCryptoImpl
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Encrypts race closes of the same key. A use-after-free shows up as a
 * throwing close, an unexpected exception, or ciphertext that doesn't
 * decrypt.
 */
@OptIn(kotlin.ExperimentalStdlibApi::class)
class ToxCryptoConcurrencyTest {
    @Test
    fun encrypt_concurrentWithClose_isSafe() {
        val passphrase = "race condition demo".encodeToByteArray()
        val plaintext = "payload".encodeToByteArray()

        val workerPairs = 32
        val iterationsPerWorker = 200

        val closeFailures = AtomicInteger(0)
        val unexpectedEncryptFailures = AtomicInteger(0)
        val sampleCiphersByWorker = arrayOfNulls<ByteArray>(workerPairs)
        val keys = Array(workerPairs) { ToxCryptoImpl.passKeyDerive(passphrase) }
        val threads = mutableListOf<Thread>()

        for (n in 0 until workerPairs) {
            val key = keys[n]

            threads +=
                thread(name = "encryptor-$n") {
                    repeat(iterationsPerWorker) {
                        try {
                            val cipher = ToxCryptoImpl.encrypt(key, plaintext)
                            if (sampleCiphersByWorker[n] == null) {
                                sampleCiphersByWorker[n] = cipher
                            }
                        } catch (_: ToxEncryptionException) {
                            // Expected once the key is closed.
                        } catch (_: Throwable) {
                            unexpectedEncryptFailures.incrementAndGet()
                        }
                    }
                }

            threads +=
                thread(name = "closer-$n") {
                    Thread.sleep((n % 5).toLong())
                    try {
                        key.close()
                    } catch (_: Throwable) {
                        closeFailures.incrementAndGet()
                    }
                }
        }

        threads.forEach { it.join() }

        assertEquals(
            0,
            closeFailures.get(),
            "close() must not throw under concurrent encrypt",
        )

        assertEquals(
            0,
            unexpectedEncryptFailures.get(),
            "encrypt() must throw only ToxEncryptionException under concurrent close",
        )

        val successfulCiphers = sampleCiphersByWorker.filterNotNull()
        assertTrue(
            successfulCiphers.isNotEmpty(),
            "expected at least one worker to complete encrypt before its close (saw 0/$workerPairs)",
        )
        for (cipher in successfulCiphers) {
            val salt = ToxCryptoImpl.getSalt(cipher)
            ToxCryptoImpl.passKeyDeriveWithSalt(passphrase, salt).use { freshKey ->
                assertContentEquals(
                    plaintext,
                    ToxCryptoImpl.decrypt(freshKey, cipher),
                    "concurrent encrypt produced content that doesn't round-trip — UAF symptom",
                )
            }
        }
    }
}
