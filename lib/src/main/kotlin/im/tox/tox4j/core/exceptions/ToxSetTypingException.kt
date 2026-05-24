package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxSetTypingException : ToxException {
    enum class Code {
        /** The friend number did not designate a valid friend. */
        FRIEND_NOT_FOUND,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
