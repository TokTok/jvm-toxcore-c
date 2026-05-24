package im.tox.tox4j.crypto

import im.tox.tox4j.core.data.ToxPassSalt

interface ToxCrypto<PassKey> {
    /**
     * Generates a secret symmetric key from the given passphrase.
     *
     * Be sure to not compromise the key! Only keep it in memory, do not write it to disk.
     *
     * Note that this function is not deterministic; to derive the same key from a password, you also must know the random salt that was used. A deterministic version of this function is [passKeyDeriveWithSalt].
     *
     * @param passphrase The user-provided password. Can be empty.
     * @param passphraseLen The length of the password.
     *
     * @return new symmetric key on success, null on failure.
     */
    fun passKeyDerive(passphrase: ByteArray): PassKey

    /**
     * Same as above, except use the given salt for deterministic key derivation.
     *
     * @param passphrase The user-provided password. Can be empty.
     * @param passphraseLen The length of the password.
     * @param salt An array of exactly [ToxCryptoConstants.SALT_LENGTH] bytes.
     *
     * @return new symmetric key on success, null on failure.
     */
    fun passKeyDeriveWithSalt(
        passphrase: ByteArray,
        salt: ToxPassSalt,
    ): PassKey

    /**
     * Encrypt a plain text with a key produced by [passKeyDerive] or [passKeyDeriveWithSalt].
     *
     * The output array must be at least `plaintext_len + [ToxCryptoConstants.ENCRYPTION_EXTRA_LENGTH]` bytes long.
     *
     * @param plaintext A byte array of length `plaintext_len`.
     * @param plaintextLen The length of the plain text array. Bigger than 0.
     * @param ciphertext The cipher text array to write the encrypted data to.
     *
     * @return true on success.
     */
    fun encrypt(
        passKey: PassKey,
        plaintext: ByteArray,
    ): ByteArray

    /**
     * This is the inverse of [encrypt], also using only keys produced by [passKeyDerive] or [passKeyDeriveWithSalt].
     *
     * @param ciphertext A byte array of length `ciphertext_len`.
     * @param ciphertextLen The length of the cipher text array. At least [ToxCryptoConstants.ENCRYPTION_EXTRA_LENGTH].
     * @param plaintext The plain text array to write the decrypted data to.
     *
     * @return true on success.
     */
    fun decrypt(
        passKey: PassKey,
        ciphertext: ByteArray,
    ): ByteArray

    /**
     * Retrieves the salt used to encrypt the given data.
     *
     * The retrieved salt can then be passed to [passKeyDeriveWithSalt] to produce the same key as was previously used. Any data encrypted with this module can be used as input.
     *
     * The cipher text must be at least [ToxCryptoConstants.ENCRYPTION_EXTRA_LENGTH] bytes in length. The salt must be [ToxCryptoConstants.SALT_LENGTH] bytes in length. If the passed byte arrays are smaller than required, the behaviour is undefined.
     *
     * If the cipher text pointer or the salt is null, this function returns false.
     *
     * Success does not say anything about the validity of the data, only that data of the appropriate size was copied.
     *
     * @param ciphertext The encrypted data; at least [ToxCryptoConstants.ENCRYPTION_EXTRA_LENGTH] bytes are read.
     * @param salt An array of exactly [ToxCryptoConstants.SALT_LENGTH] bytes to write the salt to.
     *
     * @return true on success.
     */
    fun getSalt(ciphertext: ByteArray): ToxPassSalt

    /**
     * Determines whether or not the given data is encrypted by this module.
     *
     * It does this check by verifying that the magic number is the one put in place by the encryption functions.
     *
     * The data must be at least [ToxCryptoConstants.ENCRYPTION_EXTRA_LENGTH] bytes in length. If the passed byte array is smaller than required, the behaviour is undefined.
     *
     * If the data pointer is null, the behaviour is undefined
     *
     * @param data The data to check; at least [ToxCryptoConstants.ENCRYPTION_EXTRA_LENGTH] bytes are read.
     *
     * @return true if the data is encrypted by this module.
     */
    fun isDataEncrypted(data: ByteArray): Boolean

    fun hash(data: ByteArray): ByteArray
}
