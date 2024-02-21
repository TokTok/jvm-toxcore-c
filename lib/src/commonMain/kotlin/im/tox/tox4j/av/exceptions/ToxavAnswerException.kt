package im.tox.tox4j.av.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxavAnswerException : ToxException {
    enum class Code {
        /**
         * Failed to initialise codecs for call session.
         */
        CODEC_INITIALIZATION,

        /**
         * The friend was valid, but they are not currently trying to initiate a call.
         * This is also returned if this client is already in a call with the friend.
         */
        FRIEND_NOT_CALLING,

        /**
         * The friend number did not designate a valid friend.
         */
        FRIEND_NOT_FOUND,

        /**
         * Audio or video bit rate is invalid.
         */
        INVALID_BIT_RATE,

        /**
         * Synchronization error occurred.
         */
        SYNC,
    }

    constructor(code: Code) : this(code, "")
    constructor(code: Code, message: String) : super(code, message)
}
