package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxGetPortException : ToxException {
    enum class Code {
        /**
         * The instance was not bound to any port.
         */
        NOT_BOUND,
    }

    constructor(code: Code, message: String = "") : super(code, message)
}
