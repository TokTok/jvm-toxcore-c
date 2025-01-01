package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxConferenceNewException : ToxException {
    enum class Code {
        /** The conference instance failed to initialize. */
        INIT,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
