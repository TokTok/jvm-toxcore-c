package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxConferencePeerQueryException : ToxException {
    enum class Code {
        /** The conference number passed did not designate a valid conference. */
        CONFERENCE_NOT_FOUND,

        /** The peer number passed did not designate a valid peer. */
        PEER_NOT_FOUND,

        /** The client is not connected to the conference. */
        NO_CONNECTION,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
