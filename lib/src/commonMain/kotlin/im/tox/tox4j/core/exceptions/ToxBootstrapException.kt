package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.JavaOnly
import im.tox.tox4j.exceptions.ToxException

class ToxBootstrapException : ToxException {
    enum class Code {
        /** One of the arguments to the function was NULL when it was not expected. */
        NULL,

        /**
         * The hostname could not be resolved to an IP address, the IP address passed was invalid,
         * or the function failed to send the initial request packet to the bootstrap node or TCP
         * relay.
         */
        BAD_HOST,

        /** The port passed was invalid. The valid port range is (1, 65535). */
        BAD_PORT,

        /** The public key was of invalid length. */
        @JavaOnly BAD_KEY,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
