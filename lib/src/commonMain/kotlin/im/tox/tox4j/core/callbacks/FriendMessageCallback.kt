package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFriendMessage
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.enums.ToxMessageType

/** This event is triggered when a message from a friend is received. */
interface FriendMessageCallback<ToxCoreState> {
    /**
     * @param friendNumber The friend number of the friend who sent the message.
     * @param type The type of the message (normal, action, ...).
     * @param message The message data they sent.
     */
    fun friendMessage(
        friendNumber: ToxFriendNumber,
        type: ToxMessageType,
        message: ToxFriendMessage,
        state: ToxCoreState,
    ): ToxCoreState = state
}
