package im.tox.tox4j.av.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxAvSendFrameException : ToxException {
    enum class Code {
        /** In case of video, one of Y, U, or V was null. In case of audio, the samples data pointer was null. */
        NULL,

        /** The friend_number passed did not designate a valid friend. */
        FRIEND_NOT_FOUND,

        /** This client is currently not in a call with the friend. */
        FRIEND_NOT_IN_CALL,

        /** Synchronization error occurred. */
        SYNC,

        /** One of the frame parameters was invalid. E.g. the resolution may be too small or too large, or the audio sampling rate may be unsupported. */
        INVALID,

        /** Either friend turned off audio or video receiving or we turned off sending for the said payload. */
        PAYLOAD_TYPE_DISABLED,

        /** Failed to push frame through RTP interface. */
        RTP_FAILED,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
