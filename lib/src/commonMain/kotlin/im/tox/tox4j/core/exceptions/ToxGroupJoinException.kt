package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when a group could not be joined.
 */
class ToxGroupJoinException : ToxException {
    enum class Code {
        /** The group instance failed to initialize. */
        INIT,

        /**
         * The chat_id pointer is set to NULL or a group with chat_id already exists. This usually
         * happens if the client attempts to create multiple sessions for the same group.
         */
        BAD_CHAT_ID,

        /** name is NULL or name_length is zero. */
        EMPTY,

        /** name exceeds TOX_MAX_NAME_LENGTH. */
        TOO_LONG,

        /**
         * Failed to set password. This usually occurs if the password exceeds
         * TOX_GROUP_MAX_PASSWORD_SIZE.
         */
        PASSWORD,

        /** There was a core error when initiating the group. */
        CORE,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
