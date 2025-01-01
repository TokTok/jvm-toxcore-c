package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxConferenceJoinException : ToxException {
    enum class Code {
        /** The cookie passed has an invalid length. */
        INVALID_LENGTH,

        /** The conference is not the expected type. This indicates an invalid cookie. */
        WRONG_TYPE,

        /** The friend number passed does not designate a valid friend. */
        FRIEND_NOT_FOUND,

        /** Client is already in this conference. */
        DUPLICATE,

        /** Conference instance failed to initialize. */
        INIT_FAIL,

        /** The join packet failed to send. */
        FAIL_SEND,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
