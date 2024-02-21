package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxSetTypingException : ToxException {
    enum class Code {
        /**
         * The friendNumber passed did not designate a valid friend.
         */
        FRIEND_NOT_FOUND,
    }

    constructor(code: Code, message: String = "") : super(code, message)
}
