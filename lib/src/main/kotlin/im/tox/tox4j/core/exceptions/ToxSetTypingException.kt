package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when setting the typing status of a friend failed.
 */
class ToxSetTypingException : ToxException {
    enum class Code {
        /** The friend number did not designate a valid friend. */
        FRIEND_NOT_FOUND,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
