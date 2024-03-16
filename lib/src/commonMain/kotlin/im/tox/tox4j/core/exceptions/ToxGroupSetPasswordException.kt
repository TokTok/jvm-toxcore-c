package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when setting a group password fails.
 */
class ToxGroupSetPasswordException : ToxException {
    enum class Code {
        /** The group number passed did not designate a valid group. */
        GROUP_NOT_FOUND,

        /** The caller does not have the required permissions to set the password. */
        PERMISSIONS,

        /** Password length exceeded TOX_GROUP_MAX_PASSWORD_SIZE. */
        TOO_LONG,

        /** The packet failed to send. */
        FAIL_SEND,

        /** The function failed to allocate enough memory for the operation. */
        MALLOC,

        /** The group is disconnected. */
        DISCONNECTED,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
