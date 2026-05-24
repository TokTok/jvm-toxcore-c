package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxFriendQueryException : ToxException {
    enum class Code {
        /** The pointer parameter for storing the query result (name, message) was NULL. Unlike the `_self_` variants of these functions, which have no effect when a parameter is null, these functions return an error in that case. */
        NULL,

        /** The friend_number did not designate a valid friend. */
        FRIEND_NOT_FOUND,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
