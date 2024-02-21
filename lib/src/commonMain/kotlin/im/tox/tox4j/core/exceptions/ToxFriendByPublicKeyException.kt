package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxFriendByPublicKeyException : ToxException {
    enum class Code {
        /**
         * No friend with the given Public Key exists on the friend list.
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
