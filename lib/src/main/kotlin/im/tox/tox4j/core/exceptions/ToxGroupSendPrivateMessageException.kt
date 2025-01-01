package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when a private message could not be sent to a group.
 */
class ToxGroupSendPrivateMessageException : ToxException {
    enum class Code {
        /** The group number passed did not designate a valid group. */
        GROUP_NOT_FOUND,

        /** The peer ID passed did not designate a valid peer. */
        PEER_NOT_FOUND,

        /** Message length exceeded TOX_GROUP_MAX_MESSAGE_LENGTH. */
        TOO_LONG,

        /** The message pointer is NULL or length is zero. */
        EMPTY,

        /** The message type is invalid. */
        BAD_TYPE,

        /** The caller does not have the required permissions to send group messages. */
        PERMISSIONS,

        /** Packet failed to send. */
        FAIL_SEND,

        /** The group is disconnected. */
        DISCONNECTED,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
