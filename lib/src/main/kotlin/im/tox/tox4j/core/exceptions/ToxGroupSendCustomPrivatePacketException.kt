package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when a custom packet could not be sent to a group.
 */
class ToxGroupSendCustomPrivatePacketException : ToxException {
    enum class Code {
        /** The group number passed did not designate a valid group. */
        GROUP_NOT_FOUND,

        /**
         * Message length exceeded TOX_GROUP_MAX_CUSTOM_LOSSY_PACKET_LENGTH if the packet was lossy,
         * or TOX_GROUP_MAX_CUSTOM_LOSSLESS_PACKET_LENGTH if the packet was lossless.
         */
        TOO_LONG,

        /** The message pointer is NULL or length is zero. */
        EMPTY,

        /** The peer ID passed did no designate a valid peer. */
        PEER_NOT_FOUND,

        /** The packet failed to send. */
        FAIL_SEND,

        /** The group is disconnected. */
        DISCONNECTED,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
