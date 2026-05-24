package im.tox.tox4j.core.data

import im.tox.tox4j.core.ToxCoreConstants

class ToxGroupPeerPublicKey(
    val value: ByteArray,
) {
    init {
        require(value.size == ToxCoreConstants.GROUP_PEER_PUBLIC_KEY_SIZE) {
            "ToxGroupPeerPublicKey must be ${ToxCoreConstants.GROUP_PEER_PUBLIC_KEY_SIZE} bytes, got ${value.size}"
        }
    }

    override fun equals(other: Any?): Boolean = this === other || (other is ToxGroupPeerPublicKey && value.contentEquals(other.value))

    override fun hashCode(): Int = value.contentHashCode()

    override fun toString(): String = "ToxGroupPeerPublicKey(<${value.size} bytes>)"
}
