package im.tox.tox4j.crypto.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxEncryptionException : ToxException {
    enum class Code {
        /**
         * The encryption itself failed.
         */
        FAILED,

        /**
         * The crypto lib was unable to derive a key from the given passphrase,
         * which is usually a lack of memory issue. The functions accepting keys
         * do not produce this error.
         */
        KEY_DERIVATION_FAILED,

        /**
         * The key or input data was null or empty.
         */
        NULL,
    }

    constructor(code: Code, message: String = "") : super(code, message)
}
