package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxGroupJoinException : ToxException {
    enum class Code {
        /** The group instance failed to initialize. */
        INIT,

        /** The chat_id pointer is set to null. */
        BAD_CHAT_ID,

        /** name is null or name_length is zero. */
        EMPTY,

        /** name exceeds [ToxCoreConstants.MAX_NAME_LENGTH]. */
        TOO_LONG,

        /** Failed to set password. This usually occurs if the password exceeds [ToxCoreConstants.GROUP_MAX_PASSWORD_SIZE]. */
        PASSWORD,

        /** There was a core error when initiating the group. */
        CORE,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
