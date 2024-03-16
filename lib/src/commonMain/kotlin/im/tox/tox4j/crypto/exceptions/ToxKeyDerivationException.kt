package im.tox.tox4j.crypto.exceptions

import im.tox.tox4j.exceptions.JavaOnly
import im.tox.tox4j.exceptions.ToxException

class ToxKeyDerivationException : ToxException {
    enum class Code {
        /** The salt was of incorrect length. */
        @JavaOnly INVALID_LENGTH,

        /**
         * The crypto lib was unable to derive a key from the given passphrase, which is usually a
         * lack of memory issue. The functions accepting keys do not produce this error.
         */
        FAILED,

        /** The passphrase was null or empty. */
        NULL,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
