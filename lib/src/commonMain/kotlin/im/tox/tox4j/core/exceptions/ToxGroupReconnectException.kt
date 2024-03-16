package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when a group could not be reconnected.
 */
class ToxGroupReconnectException : ToxException {
    enum class Code {
        /** The group number passed did not designate a valid group. */
        GROUP_NOT_FOUND,

        /** There was a core error when initiating the group. */
        CORE,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
