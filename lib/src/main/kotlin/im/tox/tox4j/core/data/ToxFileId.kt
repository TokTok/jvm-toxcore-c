package im.tox.tox4j.core.data

import im.tox.tox4j.core.ToxCoreConstants

class ToxFileId(
    val value: ByteArray,
) {
    init {
        require(value.size == ToxCoreConstants.FILE_ID_LENGTH) {
            "ToxFileId must be ${ToxCoreConstants.FILE_ID_LENGTH} bytes, got ${value.size}"
        }
    }

    override fun equals(other: Any?): Boolean = this === other || (other is ToxFileId && value.contentEquals(other.value))

    override fun hashCode(): Int = value.contentHashCode()

    override fun toString(): String = "ToxFileId(<${value.size} bytes>)"
}
