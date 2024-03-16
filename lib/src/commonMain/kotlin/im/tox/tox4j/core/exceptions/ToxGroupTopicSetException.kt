package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when a group topic could not be set.
 */
class ToxGroupTopicSetException : ToxException {
    enum class Code {
        /** The group number passed did not designate a valid group. */
        GROUP_NOT_FOUND,

        /** Topic length exceeded TOX_GROUP_MAX_TOPIC_LENGTH. */
        TOO_LONG,

        /** The caller does not have the required permissions to set the topic. */
        PERMISSIONS,

        /**
         * The packet could not be created. This error is usually related to cryptographic signing.
         */
        FAIL_CREATE,

        /** The packet failed to send. */
        FAIL_SEND,

        /** The group is disconnected. */
        DISCONNECTED,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
