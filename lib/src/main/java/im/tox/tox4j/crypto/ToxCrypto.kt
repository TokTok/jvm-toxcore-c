package im.tox.tox4j.crypto

/**
 * To perform encryption, first derive an encryption key from a password with
 * [[ToxCrypto.passKeyDerive]], and use the returned key to encrypt the data.
 *
 * The encrypted data is prepended with a magic number, to aid validity checking (no guarantees are
 * made of course). Any data to be decrypted must start with the magic number.
 *
 * Clients should consider alerting their users that, unlike plain data, if even one bit becomes
 * corrupted, the data will be entirely unrecoverable. Ditto if they forget their password, there is
 * no way to recover the data.
 */
interface ToxCrypto<PassKey> {
    /**
     * Compares two [[PassKey]]s for equality.
     *
     * @return true if the [[PassKey]]s are equal.
     */
    fun passKeyEquals(
        a: PassKey,
        b: PassKey,
    ): Boolean

    /**
     * Serialise the [[PassKey]] to a byte sequence.
     *
     * @return A sequence of bytes making up a [[PassKey]].
     */
    fun passKeyToBytes(passKey: PassKey): List<Byte>

    /**
     * Deserialise a [[PassKey]] from a byte sequence.
     *
     * @return [[Some]]([[PassKey]]) if the key was valid, [[None]] otherwise.
     */
    fun passKeyFromBytes(bytes: List<Byte>): PassKey?

    /**
     * Generates a secret symmetric key from the given passphrase.
     *
     * Be sure to not compromise the key! Only keep it in memory, do not write to disk. The key should
     * only be used with the other functions in this module, as it includes a salt.
     *
     * Note that this function is not deterministic; to derive the same key from a password, you also
     * must know the random salt that was used. See below.
     *
     * @param passphrase A non-empty byte array containing the passphrase.
     * @return the generated symmetric key.
     */
    // @throws[ToxKeyDerivationException]
    fun passKeyDerive(passphrase: ByteArray): PassKey

    /**
     * Same as above, except use the given salt for deterministic key derivation.
     *
     * @param passphrase A non-empty byte array containing the passphrase.
     * @param salt Array of size [[ToxCryptoConstants.SaltLength]].
     */
    // @throws[ToxKeyDerivationException]
    fun passKeyDeriveWithSalt(
        passphrase: ByteArray,
        salt: ByteArray,
    ): PassKey

    /**
     * This retrieves the salt used to encrypt the given data, which can then be passed to
     * [[passKeyDeriveWithSalt]] to produce the same key as was previously used. Any encrypted data
     * with this module can be used as input.
     *
     * Success does not say anything about the validity of the data, only that data of the appropriate
     * size was copied.
     *
     * @return the salt, or an empty array if the magic number did not match.
     */
    // @throws[ToxGetSaltException]
    fun getSalt(data: ByteArray): ByteArray

    // Now come the functions that are analogous to the part 2 functions.

    /**
     * Encrypt arbitrary data with a key produced by [[passKeyDerive]] or [[passKeyDeriveWithSalt]].
     *
     * The output array will be [[ToxCryptoConstants.EncryptionExtraLength]] bytes longer than the
     * input array.
     *
     * The result will be different on each call.
     *
     * @return the encrypted output array.
     */
    // @throws[ToxEncryptionException]
    fun encrypt(
        data: ByteArray,
        passKey: PassKey,
    ): ByteArray

    /**
     * This is the inverse of [[encrypt]], also using only keys produced by [[passKeyDerive]].
     *
     * The output data has size data_length - [[ToxCryptoConstants.EncryptionExtraLength]].
     *
     * @return the decrypted output array.
     */
    // @throws[ToxDecryptionException]
    fun decrypt(
        data: ByteArray,
        passKey: PassKey,
    ): ByteArray

    /** Determines whether or not the given data is encrypted (by checking the magic number) */
    fun isDataEncrypted(data: ByteArray): Boolean

    /**
     * Generates a cryptographic hash of the given data.
     *
     * This function may be used by clients for any purpose, but is provided primarily for validating
     * cached avatars. This use is highly recommended to avoid unnecessary avatar updates.
     *
     * This function is a wrapper to internal message-digest functions.
     *
     * @param data Data to be hashed.
     * @return hash of the data.
     */
    fun hash(data: ByteArray): ByteArray
}
