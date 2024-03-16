package im.tox.tox4j.crypto.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxGetSaltException : ToxException {
    enum class Code {
        /**
         * The input data is missing the magic number (i.e. wasn't created by this module, or is
         * corrupted)
         */
        BAD_FORMAT,

        /** The data or salt were null. */
        NULL,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
