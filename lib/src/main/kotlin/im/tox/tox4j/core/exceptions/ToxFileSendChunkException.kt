package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when a file send chunk could not be sent.
 */
class ToxFileSendChunkException : ToxException {
    enum class Code {
        /** The length parameter was non-zero, but data was NULL. */
        NULL,

        /** The friendNumber passed did not designate a valid friend. */
        FRIEND_NOT_FOUND,

        /** This client is currently not connected to the friend. */
        FRIEND_NOT_CONNECTED,

        /** No file transfer with the given file number was found for the given friend. */
        NOT_FOUND,

        /**
         * File transfer was found but isn't in a transferring state: (paused, done, broken, etc...)
         * (happens only when not called from the request chunk callback).
         */
        NOT_TRANSFERRING,

        /**
         * Attempted to send more or less data than requested. The requested data size is adjusted
         * according to maximum transmission unit and the expected end of the file. Trying to send
         * less or more than requested will return this error.
         */
        INVALID_LENGTH,

        /** Packet queue is full. */
        SENDQ,

        /** Position parameter was wrong. */
        WRONG_POSITION,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
