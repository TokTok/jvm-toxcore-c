package im.tox.tox4j.core.data

import im.tox.tox4j.crypto.ToxCryptoConstants

class ToxPassSalt(
    val value: ByteArray,
) {
    init {
        require(value.size == ToxCryptoConstants.SALT_LENGTH) {
            "ToxPassSalt must be ${ToxCryptoConstants.SALT_LENGTH} bytes, got ${value.size}"
        }
    }

    override fun equals(other: Any?): Boolean = this === other || (other is ToxPassSalt && value.contentEquals(other.value))

    override fun hashCode(): Int = value.contentHashCode()

    override fun toString(): String = "ToxPassSalt(<${value.size} bytes>)"
}
