package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

class ToxEventsIterateException : ToxException {
    enum class Code {
        /**
         * The function failed to allocate enough memory to store the events.
         *
         * Some events may still be stored if the return value is null. The events object will always be valid (or null) but if this error code is set, the function may have missed some events.
         */
        MALLOC,
    }

    constructor(code: Code) : this(code, "")

    constructor(code: Code, message: String) : super(code, message)
}
