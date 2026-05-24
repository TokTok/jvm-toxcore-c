package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.enums.ToxUserStatus

interface FriendStatusCallback<ToxCoreState> {
    /**
     * @param friendNumber The friend number of the friend whose user status changed.
     * @param status The new user status.
     */
    fun friendStatus(
        friendNumber: ToxFriendNumber,
        status: ToxUserStatus,
        state: ToxCoreState,
    ): ToxCoreState = state
}
