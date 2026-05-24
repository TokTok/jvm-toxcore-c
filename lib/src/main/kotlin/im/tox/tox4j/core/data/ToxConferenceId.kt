package im.tox.tox4j.core.data

import im.tox.tox4j.core.ToxCoreConstants

class ToxConferenceId(
    val value: ByteArray,
) {
    init {
        require(value.size == ToxCoreConstants.CONFERENCE_ID_SIZE) {
            "ToxConferenceId must be ${ToxCoreConstants.CONFERENCE_ID_SIZE} bytes, got ${value.size}"
        }
    }

    override fun equals(other: Any?): Boolean = this === other || (other is ToxConferenceId && value.contentEquals(other.value))

    override fun hashCode(): Int = value.contentHashCode()

    override fun toString(): String = "ToxConferenceId(<${value.size} bytes>)"
}
