package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when a file seek could not be performed.
 */
class ToxFileSeekException : ToxException {
    enum class Code {
        /** The friendNumber passed did not designate a valid friend. */
        FRIEND_NOT_FOUND,

        /** This client is currently not connected to the friend. */
        FRIEND_NOT_CONNECTED,

        /** No file transfer with the given file number was found for the given friend. */
        NOT_FOUND,

        /** File was not in a state where it could be seeked. */
        DENIED,

        /** Seek position was invalid */
        INVALID_POSITION,

        /** Packet queue is full. */
        SENDQ,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
