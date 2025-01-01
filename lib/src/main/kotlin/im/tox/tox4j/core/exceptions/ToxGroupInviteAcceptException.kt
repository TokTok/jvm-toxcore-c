package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when a group invite could not be accepted.
 */
class ToxGroupInviteAcceptException : ToxException {
    enum class Code {
        /** The invite data is not in the expected format. */
        BAD_INVITE,

        /** The group instance failed to initialize. */
        INIT_FAILED,

        /** name exceeds TOX_MAX_NAME_LENGTH */
        TOO_LONG,

        /** name is NULL or name_length is zero. */
        EMPTY,

        /**
         * Failed to set password. This usually occurs if the password exceeds
         * TOX_GROUP_MAX_PASSWORD_SIZE.
         */
        PASSWORD,

        /** The friend number passed did not designate a valid friend. */
        FRIEND_NOT_FOUND,

        /** Packet failed to send. */
        FAIL_SEND,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
