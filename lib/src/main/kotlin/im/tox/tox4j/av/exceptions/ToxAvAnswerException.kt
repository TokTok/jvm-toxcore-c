package im.tox.tox4j.av.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxAvAnswerException : ToxException {
    enum class Code {
        /** Synchronization error occurred. */
        SYNC,

        /** Failed to initialize codecs for call session. Note that codec initiation will fail if there is no receive callback registered for either audio or video. */
        CODEC_INITIALIZATION,

        /** The friend number did not designate a valid friend. */
        FRIEND_NOT_FOUND,

        /** The friend was valid, but they are not currently trying to initiate a call. This is also returned if this client is already in a call with the friend. */
        FRIEND_NOT_CALLING,

        /** Audio or video bit rate is invalid. */
        INVALID_BIT_RATE,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
