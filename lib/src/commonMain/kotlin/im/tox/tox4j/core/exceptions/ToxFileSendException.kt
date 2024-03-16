package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when a file could not be sent.
 */
class ToxFileSendException : ToxException {
    enum class Code {
        /** One of the arguments to the function was NULL when it was not expected. */
        NULL,

        /** The friendNumber passed did not designate a valid friend. */
        FRIEND_NOT_FOUND,

        /** This client is currently not connected to the friend. */
        FRIEND_NOT_CONNECTED,

        /** Filename length exceeded TOX_MAX_FILENAME_LENGTH bytes. */
        NAME_TOO_LONG,

        /**
         * Too many ongoing transfers. The maximum number of concurrent file transfers is 256 per
         * friend per direction (sending and receiving).
         */
        TOO_MANY,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
