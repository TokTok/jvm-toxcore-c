package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxGroupMessageId
import im.tox.tox4j.core.data.ToxGroupNumber
import im.tox.tox4j.core.data.ToxGroupPeerNumber
import im.tox.tox4j.core.enums.ToxMessageType

/** This event is triggered when the client receives a private message. */
interface GroupPrivateMessageCallback<ToxCoreState> {
    /**
     * @param groupNumber The group number of the group the private message is intended for.
     * @param peerId The ID of the peer who sent the private message.
     * @param messageType The type of message (normal, action, ...).
     * @param message The message data.
     * @param messageId A pseudo message id that clients can use to uniquely identify this group
     *   message.
     */
    fun groupPrivateMessage(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
        messageType: ToxMessageType,
        message: ByteArray,
        messageId: ToxGroupMessageId,
        state: ToxCoreState,
    ): ToxCoreState = state
}
