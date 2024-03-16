package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

/**
 * An exception thrown when a new Tox instance could not be created.
 */
class ToxNewException : ToxException {
    enum class Code {
        /** One of the arguments to the function was NULL when it was not expected. */
        NULL,

        /**
         * The function was unable to allocate enough memory to store the internal structures for
         * the Tox object.
         */
        MALLOC,

        /**
         * The function was unable to bind to a port. This may mean that all ports have already been
         * bound, e.g. by other Tox instances, or it may mean a permission error. You may be able to
         * gather more information from errno.
         */
        PORT_ALLOC,

        /** proxy_type was invalid. */
        PROXY_BAD_TYPE,

        /** proxy_type was valid but the proxy_host passed had an invalid format or was NULL. */
        PROXY_BAD_HOST,

        /** proxy_type was valid, but the proxy_port was invalid. */
        PROXY_BAD_PORT,

        /** The proxy address passed could not be resolved. */
        PROXY_NOT_FOUND,

        /** The byte array to be loaded contained an encrypted save. */
        LOAD_ENCRYPTED,

        /**
         * The data format was invalid. This can happen when loading data that was saved by an older
         * version of Tox, or when the data has been corrupted. When loading from badly formatted
         * data, some data may have been loaded, and the rest is discarded. Passing an invalid
         * length parameter also causes this error.
         */
        LOAD_BAD_FORMAT,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
