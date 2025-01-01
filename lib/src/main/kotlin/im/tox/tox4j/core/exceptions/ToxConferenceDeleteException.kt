package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxConferenceDeleteException : ToxException {
    enum class Code {
        /** The conference number passed did not designate a valid conference. */
        CONFERENCE_NOT_FOUND,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
