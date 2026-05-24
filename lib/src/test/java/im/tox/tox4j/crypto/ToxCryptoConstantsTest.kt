package im.tox.tox4j.crypto

import kotlin.test.Test
import kotlin.test.assertEquals

class ToxCryptoConstantsTest {
    @Test
    fun saltLength() = assertEquals(32, ToxCryptoConstants.SALT_LENGTH)

    @Test
    fun keyLength() = assertEquals(32, ToxCryptoConstants.KEY_LENGTH)

    @Test
    fun encryptionExtraLength() = assertEquals(80, ToxCryptoConstants.ENCRYPTION_EXTRA_LENGTH)

    @Test
    fun hashLength() = assertEquals(32, ToxCryptoConstants.HASH_LENGTH)

    @Test
    fun publicKeyLength() = assertEquals(32, ToxCryptoConstants.PUBLIC_KEY_LENGTH)

    @Test
    fun secretKeyLength() = assertEquals(32, ToxCryptoConstants.SECRET_KEY_LENGTH)

    @Test
    fun sharedKeyLength() = assertEquals(32, ToxCryptoConstants.SHARED_KEY_LENGTH)

    @Test
    fun nonceLength() = assertEquals(24, ToxCryptoConstants.NONCE_LENGTH)
}
