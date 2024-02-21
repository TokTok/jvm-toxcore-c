package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxFileGetException : ToxException {
    enum class Code {
        /**
         * The friendNumber passed did not designate a valid friend.
         */
        FRIEND_NOT_FOUND,

        /**
         * No file transfer with the given file number was found for the given friend.
         */
        NOT_FOUND,

        /**
         * An argument was null.
         */
        NULL,
    }

    constructor(code: Code) : this(code, "")
    constructor(code: Code, message: String) : super(code, message)
}
