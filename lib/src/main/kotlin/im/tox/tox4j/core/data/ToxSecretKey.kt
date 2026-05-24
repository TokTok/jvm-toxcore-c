package im.tox.tox4j.core.data

import im.tox.tox4j.core.ToxCoreConstants

class ToxSecretKey(
    val value: ByteArray,
) {
    init {
        require(value.size == ToxCoreConstants.SECRET_KEY_SIZE) {
            "ToxSecretKey must be ${ToxCoreConstants.SECRET_KEY_SIZE} bytes, got ${value.size}"
        }
    }

    override fun equals(other: Any?): Boolean = this === other || (other is ToxSecretKey && value.contentEquals(other.value))

    override fun hashCode(): Int = value.contentHashCode()

    override fun toString(): String = "ToxSecretKey(<${value.size} bytes>)"
}
