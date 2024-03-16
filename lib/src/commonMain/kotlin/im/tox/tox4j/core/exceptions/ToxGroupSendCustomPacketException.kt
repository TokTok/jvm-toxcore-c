package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when a custom packet could not be sent to a group.
 */
class ToxGroupSendCustomPacketException : ToxException {
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

        /** The group is disconnected. */
        DISCONNECTED,

        /**
         * The packet did not successfully send to any peer. This often indicates a connection issue
         * on the sender's side.
         */
        FAIL_SEND,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
