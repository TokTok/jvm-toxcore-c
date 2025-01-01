package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when a file could not be retrieved.
 */
class ToxFileGetException : ToxException {
    enum class Code {
        /** One of the arguments to the function was NULL when it was not expected. */
        NULL,

        /** The friendNumber passed did not designate a valid friend. */
        FRIEND_NOT_FOUND,

        /** No file transfer with the given file number was found for the given friend. */
        NOT_FOUND,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
