package im.tox.tox4j.impl.jni

import im.tox.tox4j.crypto.ToxCrypto
import im.tox.tox4j.crypto.ToxCryptoConstants

private typealias PassKey = ByteArray

object ToxCryptoImpl : ToxCrypto<ByteArray> {
    override fun passKeyEquals(
        a: PassKey,
        b: PassKey,
    ): Boolean = a.contentEquals(b)

    override fun passKeyToBytes(passKey: PassKey): List<Byte> = passKey.toList()

    override fun passKeyFromBytes(bytes: List<Byte>): PassKey? =
        if (bytes.size == ToxCryptoConstants.KeyLength + ToxCryptoConstants.SaltLength) {
            bytes.toByteArray()
        } else {
            null
        }

    override fun encrypt(
        data: ByteArray,
        passKey: PassKey,
    ): ByteArray = ToxCryptoJni.toxPassKeyEncrypt(data, passKey)

    override fun getSalt(data: ByteArray): ByteArray = ToxCryptoJni.toxGetSalt(data)

    override fun isDataEncrypted(data: ByteArray): Boolean = ToxCryptoJni.toxIsDataEncrypted(data)

    override fun passKeyDeriveWithSalt(
        passphrase: ByteArray,
        salt: ByteArray,
    ): PassKey = ToxCryptoJni.toxPassKeyDeriveWithSalt(passphrase, salt)

    override fun passKeyDerive(passphrase: ByteArray): PassKey = ToxCryptoJni.toxPassKeyDerive(passphrase)

    override fun decrypt(
        data: ByteArray,
        passKey: PassKey,
    ): ByteArray = ToxCryptoJni.toxPassKeyDecrypt(data, passKey)

    override fun hash(data: ByteArray): ByteArray = ToxCryptoJni.toxHash(data)
}
