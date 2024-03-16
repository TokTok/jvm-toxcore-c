package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when a new group could not be created.
 */
class ToxGroupNewException : ToxException {
    enum class Code {
        /**
         * name exceeds TOX_MAX_NAME_LENGTH or group_name exceeded TOX_GROUP_MAX_GROUP_NAME_LENGTH.
         */
        TOO_LONG,

        /** name or group_name is NULL or length is zero. */
        EMPTY,

        /** The group instance failed to initialize. */
        INIT,

        /**
         * The group state failed to initialize. This usually indicates that something went wrong
         * related to cryptographic signing.
         */
        STATE,

        /** The group failed to announce to the DHT. This indicates a network related error. */
        ANNOUNCE,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
