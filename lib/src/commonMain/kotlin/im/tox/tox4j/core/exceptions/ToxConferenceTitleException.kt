package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxConferenceTitleException : ToxException {
    enum class Code {
        /** The conference number passed did not designate a valid conference. */
        CONFERENCE_NOT_FOUND,

        /** The title is too long or empty. */
        INVALID_LENGTH,

        /** The title packet failed to send. */
        FAIL_SEND,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
