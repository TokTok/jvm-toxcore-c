package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when a friend could not be added.
 */
class ToxFriendAddException : ToxException {
    enum class Code {
        /** One of the arguments to the function was NULL when it was not expected. */
        NULL,

        /** The length of the friend request message exceeded TOX_MAX_FRIEND_REQUEST_LENGTH. */
        TOO_LONG,

        /**
         * The friend request message was empty. This, and the TOO_LONG code will never be returned
         * from tox_friend_add_norequest.
         */
        NO_MESSAGE,

        /** The friend address belongs to the sending client. */
        OWN_KEY,

        /**
         * A friend request has already been sent, or the address belongs to a friend that is
         * already on the friend list.
         */
        ALREADY_SENT,

        /** The friend address checksum failed. */
        BAD_CHECKSUM,

        /** The friend was already there, but the nospam value was different. */
        SET_NEW_NOSPAM,

        /** A memory allocation failed when trying to increase the friend list size. */
        MALLOC,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
