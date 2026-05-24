package im.tox.tox4j.impl.jni

import im.tox.tox4j.core.data.ToxPassSalt
import im.tox.tox4j.crypto.ToxCrypto

object ToxCryptoImpl : ToxCrypto<PassKey> {
    override fun passKeyDerive(passphrase: ByteArray): PassKey = PassKey(ToxCryptoJni.toxPassKeyDerive(passphrase))

    override fun passKeyDeriveWithSalt(
        passphrase: ByteArray,
        salt: ToxPassSalt,
    ): PassKey = PassKey(ToxCryptoJni.toxPassKeyDeriveWithSalt(passphrase, salt.value))

    override fun encrypt(
        passKey: PassKey,
        plaintext: ByteArray,
    ): ByteArray = ToxCryptoJni.toxPassKeyEncrypt(passKey.instanceNumber, plaintext)

    override fun decrypt(
        passKey: PassKey,
        ciphertext: ByteArray,
    ): ByteArray = ToxCryptoJni.toxPassKeyDecrypt(passKey.instanceNumber, ciphertext)

    override fun getSalt(ciphertext: ByteArray): ToxPassSalt = ToxPassSalt(ToxCryptoJni.toxGetSalt(ciphertext))

    override fun isDataEncrypted(data: ByteArray): Boolean = ToxCryptoJni.toxIsDataEncrypted(data)

    override fun hash(data: ByteArray): ByteArray = ToxCryptoJni.toxHash(data)
}
