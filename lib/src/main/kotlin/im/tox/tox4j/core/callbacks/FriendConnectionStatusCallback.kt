package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.enums.ToxConnection

/**
 * This event is triggered when a friend goes offline after having been online, or when a friend
 * goes online.
 *
 * This callback is not called when adding friends. It is assumed that when adding friends, their
 * connection status is initially offline.
 */
interface FriendConnectionStatusCallback<ToxCoreState> {
    /**
     * @param friendNumber The friend number of the friend whose connection status changed.
     * @param connectionStatus The result of calling tox_friend_get_connection_status on the passed
     *   friendNumber.
     */
    fun friendConnectionStatus(
        friendNumber: ToxFriendNumber,
        connectionStatus: ToxConnection,
        state: ToxCoreState,
    ): ToxCoreState = state
}
