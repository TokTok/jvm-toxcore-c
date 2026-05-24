package im.tox.tox4j.core.data

import im.tox.tox4j.core.ToxCoreConstants

class ToxPublicKey(
    val value: ByteArray,
) {
    init {
        require(value.size == ToxCoreConstants.PUBLIC_KEY_SIZE) {
            "ToxPublicKey must be ${ToxCoreConstants.PUBLIC_KEY_SIZE} bytes, got ${value.size}"
        }
    }

    override fun equals(other: Any?): Boolean = this === other || (other is ToxPublicKey && value.contentEquals(other.value))

    override fun hashCode(): Int = value.contentHashCode()

    override fun toString(): String = "ToxPublicKey(<${value.size} bytes>)"
}
