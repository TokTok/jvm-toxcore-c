package im.tox.tox4j.av.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxAvBitRateSetException : ToxException {
    enum class Code {
        /** Synchronization error occurred. */
        SYNC,

        /** The bit rate passed was not one of the supported values. */
        INVALID_BIT_RATE,

        /** The friend_number passed did not designate a valid friend. */
        FRIEND_NOT_FOUND,

        /** This client is currently not in a call with the friend. */
        FRIEND_NOT_IN_CALL,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
