package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when a file transfer control could not be sent.
 */
class ToxFileControlException : ToxException {
    enum class Code {
        /** The friendNumber passed did not designate a valid friend. */
        FRIEND_NOT_FOUND,

        /** This client is currently not connected to the friend. */
        FRIEND_NOT_CONNECTED,

        /** No file transfer with the given file number was found for the given friend. */
        NOT_FOUND,

        /** A RESUME control was sent, but the file transfer is running normally. */
        NOT_PAUSED,

        /**
         * A RESUME control was sent, but the file transfer was paused by the other party. Only the
         * party that paused the transfer can resume it.
         */
        DENIED,

        /** A PAUSE control was sent, but the file transfer was already paused. */
        ALREADY_PAUSED,

        /** Packet queue is full. */
        SENDQ,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
