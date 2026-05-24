package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.enums.ToxConnection

interface FriendConnectionStatusCallback<ToxCoreState> {
    /**
     * @param friendNumber The friend number of the friend whose connection status changed.
     * @param connectionStatus The result of calling `tox_friend_get_connection_status` on the passed friend_number.
     */
    fun friendConnectionStatus(
        friendNumber: ToxFriendNumber,
        connectionStatus: ToxConnection,
        state: ToxCoreState,
    ): ToxCoreState = state
}
