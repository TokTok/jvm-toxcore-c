package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when a role could not be set in a group.
 */
class ToxGroupSetRoleException : ToxException {
    enum class Code {
        /** The group number passed did not designate a valid group. */
        GROUP_NOT_FOUND,

        /** The ID passed did not designate a valid peer. Note: you cannot set your own role. */
        PEER_NOT_FOUND,

        /** The caller does not have the required permissions for this action. */
        PERMISSIONS,

        /**
         * The role assignment is invalid. This will occur if you try to set a peer's role to the
         * role they already have.
         */
        ASSIGNMENT,

        /**
         * The role was not successfully set. This may occur if the packet failed to send, or if the
         * role limit has been reached.
         */
        FAIL_ACTION,

        /** The caller attempted to set their own role. */
        SELF,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
