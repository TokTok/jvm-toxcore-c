package im.tox.tox4j.exceptions

abstract class ToxException constructor(val code: Enum<*>, private val extraMessage: String) :
    Exception(extraMessage) {
    final override val message: String
        get() =
            when (extraMessage) {
                "" -> "Error code: " + code.name
                else -> extraMessage + ", error code: " + code.name
            }
}
