package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFriendNumber

interface FriendStatusMessageCallback<ToxCoreState> {
    /**
     * @param friendNumber The friend number of the friend whose status message changed.
     * @param message A byte array containing the same data as `tox_friend_get_status_message` would write to its `status_message` parameter.
     */
    fun friendStatusMessage(
        friendNumber: ToxFriendNumber,
        message: ByteArray,
        state: ToxCoreState,
    ): ToxCoreState = state
}
