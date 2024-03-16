package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when a custom packet could not be sent to a friend.
 */
class ToxFriendCustomPacketException : ToxException {
    enum class Code {
        /** One of the arguments to the function was NULL when it was not expected. */
        NULL,

        /** The friend number did not designate a valid friend. */
        FRIEND_NOT_FOUND,

        /** This client is currently not connected to the friend. */
        FRIEND_NOT_CONNECTED,

        /**
         * The first byte of data was not in the specified range for the packet type. This range is
         * 192-254 for lossy, and 69, 160-191 for lossless packets.
         */
        INVALID,

        /** Attempted to send an empty packet. */
        EMPTY,

        /** Packet data length exceeded TOX_MAX_CUSTOM_PACKET_SIZE. */
        TOO_LONG,

        /** Packet queue is full. */
        SENDQ,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
