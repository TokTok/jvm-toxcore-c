package im.tox.tox4j.core.data

import im.tox.tox4j.crypto.ToxCryptoConstants

class ToxHash(
    val value: ByteArray,
) {
    init {
        require(value.size == ToxCryptoConstants.HASH_LENGTH) {
            "ToxHash must be ${ToxCryptoConstants.HASH_LENGTH} bytes, got ${value.size}"
        }
    }

    override fun equals(other: Any?): Boolean = this === other || (other is ToxHash && value.contentEquals(other.value))

    override fun hashCode(): Int = value.contentHashCode()

    override fun toString(): String = "ToxHash(<${value.size} bytes>)"
}
