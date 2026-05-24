package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFriendNumber

interface FriendTypingCallback<ToxCoreState> {
    /**
     * @param friendNumber The friend number of the friend who started or stopped typing.
     * @param typing The result of calling [friendGetTyping] on the passed friend_number.
     */
    fun friendTyping(
        friendNumber: ToxFriendNumber,
        typing: Boolean,
        state: ToxCoreState,
    ): ToxCoreState = state
}
