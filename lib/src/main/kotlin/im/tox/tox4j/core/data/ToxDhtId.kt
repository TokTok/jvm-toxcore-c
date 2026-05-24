package im.tox.tox4j.core.data

import im.tox.tox4j.core.ToxCoreConstants

class ToxDhtId(
    val value: ByteArray,
) {
    init {
        require(value.size == ToxCoreConstants.DHT_ID_SIZE) {
            "ToxDhtId must be ${ToxCoreConstants.DHT_ID_SIZE} bytes, got ${value.size}"
        }
    }

    override fun equals(other: Any?): Boolean = this === other || (other is ToxDhtId && value.contentEquals(other.value))

    override fun hashCode(): Int = value.contentHashCode()

    override fun toString(): String = "ToxDhtId(<${value.size} bytes>)"
}
