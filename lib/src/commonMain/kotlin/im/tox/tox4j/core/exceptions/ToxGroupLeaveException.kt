package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when a group could not be left.
 */
class ToxGroupLeaveException : ToxException {
    enum class Code {
        /** The group number passed did not designate a valid group. */
        GROUP_NOT_FOUND,

        /** Message length exceeded TOX_GROUP_MAX_PART_LENGTH. */
        TOO_LONG,

        /** The parting packet failed to send. */
        FAIL_SEND,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
