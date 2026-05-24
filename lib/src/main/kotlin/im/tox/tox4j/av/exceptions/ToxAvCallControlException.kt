package im.tox.tox4j.av.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxAvCallControlException : ToxException {
    enum class Code {
        /** Synchronization error occurred. */
        SYNC,

        /** The friend_number passed did not designate a valid friend. */
        FRIEND_NOT_FOUND,

        /** This client is currently not in a call with the friend. Before the call is answered, only CANCEL is a valid control. */
        FRIEND_NOT_IN_CALL,

        /** Happens if user tried to pause an already paused call or if trying to resume a call that is not paused. */
        INVALID_TRANSITION,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
