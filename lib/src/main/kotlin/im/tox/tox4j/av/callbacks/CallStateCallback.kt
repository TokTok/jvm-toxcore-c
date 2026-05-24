package im.tox.tox4j.av.callbacks

import im.tox.tox4j.core.data.ToxFriendNumber

interface CallStateCallback<ToxCoreState> {
    /**
     * The function type for the call_state callback.
     *
     * @param friendNumber The friend number for which the call state changed.
     * @param state The bitmask of the new call state which is guaranteed to be different than the previous state. The state is set to 0 when the call is paused. The bitmask represents all the activities currently performed by the friend.
     */
    fun callState(
        friendNumber: ToxFriendNumber,
        callState: Int,
        state: ToxCoreState,
    ): ToxCoreState = state
}
