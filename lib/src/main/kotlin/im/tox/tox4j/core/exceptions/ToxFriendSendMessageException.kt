package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when a message could not be sent to a friend.
 */
class ToxFriendSendMessageException : ToxException {
    enum class Code {
        /** One of the arguments to the function was NULL when it was not expected. */
        NULL,

        /** The friend number did not designate a valid friend. */
        FRIEND_NOT_FOUND,

        /** This client is currently not connected to the friend. */
        FRIEND_NOT_CONNECTED,

        /** An allocation error occurred while increasing the send queue size. */
        SENDQ,

        /** Message length exceeded TOX_MAX_MESSAGE_LENGTH. */
        TOO_LONG,

        /** Attempted to send a zero-length message. */
        EMPTY,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
