package im.tox.tox4j.crypto.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxDecryptionException : ToxException {
    enum class Code {
        /** One of the arguments to the function was null when it was not expected. */
        NULL,

        /** The input data was shorter than [ToxCryptoConstants.ENCRYPTION_EXTRA_LENGTH] bytes */
        INVALID_LENGTH,

        /** The input data is missing the magic number (i.e. wasn't created by this module, or is corrupted). */
        BAD_FORMAT,

        /** The crypto lib was unable to derive a key from the given passphrase, which is usually a lack of memory issue. The functions accepting keys do not produce this error. */
        KEY_DERIVATION_FAILED,

        /** The encrypted byte array could not be decrypted. Either the data was corrupted or the password/key was incorrect. */
        FAILED,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
