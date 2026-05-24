package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxFriendCustomPacketException : ToxException {
    enum class Code {
        /** One of the arguments to the function was null when it was not expected. */
        NULL,

        /** The friend number did not designate a valid friend. */
        FRIEND_NOT_FOUND,

        /** This client is currently not connected to the friend. */
        FRIEND_NOT_CONNECTED,

        /** The first byte of data was not one of the permitted values; for lossy packets the first byte must be in the range 192-254, and for lossless packets it must be either 69 or in the range 160-191. */
        INVALID,

        /** Attempted to send an empty packet. */
        EMPTY,

        /** Packet data length exceeded [ToxCoreConstants.MAX_CUSTOM_PACKET_SIZE]. */
        TOO_LONG,

        /** Packet queue is full. */
        SENDQ,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
