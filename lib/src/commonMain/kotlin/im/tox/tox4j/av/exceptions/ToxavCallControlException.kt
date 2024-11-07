package im.tox.tox4j.av.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxavCallControlException : ToxException {
    enum class Code {
        /** The friend number did not designate a valid friend. */
        FRIEND_NOT_FOUND,

        /**
         * This client is currently not in a call with the friend. Before the call is answered, only
         * CANCEL is a valid control
         */
        FRIEND_NOT_IN_CALL,

        /**
         * Happens if user tried to pause an already paused call or if trying to resume a call that
         * is not paused.
         */
        INVALID_TRANSITION,

        /** Synchronization error occurred. */
        SYNC,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
