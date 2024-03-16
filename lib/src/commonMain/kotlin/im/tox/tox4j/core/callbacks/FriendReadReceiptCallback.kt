package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFriendMessageId
import im.tox.tox4j.core.data.ToxFriendNumber

/**
 * This event is triggered when the friend receives the message sent with tox_friend_send_message
 * with the corresponding message ID.
 */
interface FriendReadReceiptCallback<ToxCoreState> {
    /**
     * @param friendNumber The friend number of the friend who received the message.
     * @param messageId The message ID as returned from tox_friend_send_message corresponding to the
     *   message sent.
     */
    fun friendReadReceipt(
        friendNumber: ToxFriendNumber,
        messageId: ToxFriendMessageId,
        state: ToxCoreState,
    ): ToxCoreState = state
}
