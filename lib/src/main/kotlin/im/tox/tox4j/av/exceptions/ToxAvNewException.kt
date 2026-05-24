package im.tox.tox4j.av.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxAvNewException : ToxException {
    enum class Code {
        /** One of the arguments to the function was null when it was not expected. */
        NULL,

        /** Memory allocation failure while trying to allocate structures required for the A/V session. */
        MALLOC,

        /** Attempted to create a second session for the same Tox instance. */
        MULTIPLE,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
