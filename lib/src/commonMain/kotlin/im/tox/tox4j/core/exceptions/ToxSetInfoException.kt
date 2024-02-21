package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxSetInfoException : ToxException {
    enum class Code {
        /**
         * An argument was null.
         */
        NULL,

        /**
         * Information length exceeded maximum permissible size.
         */
        TOO_LONG,
    }

    constructor(code: Code) : this(code, "")
    constructor(code: Code, message: String) : super(code, message)
}
