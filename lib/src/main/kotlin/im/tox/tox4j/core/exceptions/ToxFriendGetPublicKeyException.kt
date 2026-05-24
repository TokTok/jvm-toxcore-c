package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxFriendGetPublicKeyException : ToxException {
    enum class Code {
        /** No friend with the given number exists on the friend list. */
        FRIEND_NOT_FOUND,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
