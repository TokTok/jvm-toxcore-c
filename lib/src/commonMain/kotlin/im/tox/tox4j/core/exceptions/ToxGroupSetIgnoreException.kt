package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when a group could not be ignored.
 */
class ToxGroupSetIgnoreException : ToxException {
    enum class Code {
        /** The group number passed did not designate a valid group. */
        GROUP_NOT_FOUND,

        /** The ID passed did not designate a valid peer. */
        PEER_NOT_FOUND,

        /** The caller attempted to ignore himself. */
        SELF,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
