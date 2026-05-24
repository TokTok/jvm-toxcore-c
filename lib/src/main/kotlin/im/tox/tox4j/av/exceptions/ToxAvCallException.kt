package im.tox.tox4j.av.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxAvCallException : ToxException {
    enum class Code {
        /** A resource allocation error occurred while trying to create the structures required for the call. */
        MALLOC,

        /** Synchronization error occurred. */
        SYNC,

        /** The friend number did not designate a valid friend. */
        FRIEND_NOT_FOUND,

        /** The friend was valid, but not currently connected. */
        FRIEND_NOT_CONNECTED,

        /** Attempted to call a friend while already in an audio or video call with them. */
        FRIEND_ALREADY_IN_CALL,

        /** Audio or video bit rate is invalid. */
        INVALID_BIT_RATE,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
