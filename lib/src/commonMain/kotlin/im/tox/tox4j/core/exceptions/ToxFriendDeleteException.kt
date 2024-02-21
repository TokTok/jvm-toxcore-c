package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxFriendDeleteException : ToxException {
    enum class Code {
        /**
         * There was no friend with the given friend number. No friends were deleted.
         */
        FRIEND_NOT_FOUND,
    }

    constructor(code: Code) : this(code, "")
    constructor(code: Code, message: String) : super(code, message)
}
