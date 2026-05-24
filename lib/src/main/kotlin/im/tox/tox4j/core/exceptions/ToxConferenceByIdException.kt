package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxConferenceByIdException : ToxException {
    enum class Code {
        /** One of the arguments to the function was null when it was not expected. */
        NULL,

        /** No conference with the given id exists on the conference list. */
        NOT_FOUND,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
