package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when a group self name could not be set.
 */
class ToxGroupSelfNameSetException : ToxException {
    enum class Code {
        /** The group number passed did not designate a valid group. */
        GROUP_NOT_FOUND,

        /** Name length exceeded TOX_MAX_NAME_LENGTH. */
        TOO_LONG,

        /** The length given to the set function is zero or name is a NULL pointer. */
        INVALID,

        /** The packet failed to send. */
        FAIL_SEND,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
