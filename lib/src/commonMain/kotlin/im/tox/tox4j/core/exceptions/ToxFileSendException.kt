package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxFileSendException : ToxException {
    enum class Code {
        /**
         * This client is currently not connected to the friend.
         */
        FRIEND_NOT_CONNECTED,

        /**
         * The friendNumber passed did not designate a valid friend.
         */
        FRIEND_NOT_FOUND,

        /**
         * Filename length exceeded {@link ToxCoreConstants#MAX_FILENAME_LENGTH} bytes.
         */
        NAME_TOO_LONG,

        /**
         * An argument was null.
         */
        NULL,

        /**
         * Too many ongoing transfers. The maximum number of concurrent file transfers
         * is 256 per friend per direction (sending and receiving, so 512 total).
         */
        TOO_MANY,
    }

    constructor(code: Code) : this(code, "")
    constructor(code: Code, message: String) : super(code, message)
}
