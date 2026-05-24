package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.enums.ToxLogLevel

interface LogCallback<ToxCoreState> {
    /**
     * This event is triggered when Tox logs an internal message.
     *
     * This is mostly useful for debugging. This callback can be called from any function, not just [iterate]. This means the user data lifetime must at least extend between registering and unregistering it or [kill].
     *
     * Other toxcore modules such as toxav may concurrently call this callback at any time. Thus, user code must make sure it is equipped to handle concurrent execution, e.g. by employing appropriate mutex locking.
     *
     * When using the experimental_thread_safety option, no Tox API functions can be called from within the log callback.
     *
     * @param level The severity of the log message.
     * @param file The source file from which the message originated.
     * @param line The source line from which the message originated.
     * @param func The function from which the message originated.
     * @param message The log message.
     * @param userData The user data pointer passed to [new] in options.
     */
    fun log(
        level: ToxLogLevel,
        file: String,
        line: Int,
        func: String,
        message: String,
        state: ToxCoreState,
    ): ToxCoreState = state
}
