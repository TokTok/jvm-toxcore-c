package im.tox.tox4j.core.data

import im.tox.tox4j.core.ToxCoreConstants

class ToxAddress(
    val value: ByteArray,
) {
    init {
        require(value.size == ToxCoreConstants.ADDRESS_SIZE) {
            "ToxAddress must be ${ToxCoreConstants.ADDRESS_SIZE} bytes, got ${value.size}"
        }
    }

    override fun equals(other: Any?): Boolean = this === other || (other is ToxAddress && value.contentEquals(other.value))

    override fun hashCode(): Int = value.contentHashCode()

    override fun toString(): String = "ToxAddress(<${value.size} bytes>)"
}
