package im.tox.tox4j.core.data

import im.tox.tox4j.core.ToxCoreConstants

class ToxGroupChatId(
    val value: ByteArray,
) {
    init {
        require(value.size == ToxCoreConstants.GROUP_CHAT_ID_SIZE) {
            "ToxGroupChatId must be ${ToxCoreConstants.GROUP_CHAT_ID_SIZE} bytes, got ${value.size}"
        }
    }

    override fun equals(other: Any?): Boolean = this === other || (other is ToxGroupChatId && value.contentEquals(other.value))

    override fun hashCode(): Int = value.contentHashCode()

    override fun toString(): String = "ToxGroupChatId(<${value.size} bytes>)"
}
