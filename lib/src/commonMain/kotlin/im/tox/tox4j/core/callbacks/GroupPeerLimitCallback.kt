package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxGroupNumber

/** This event is triggered when the group founder changes the maximum peer limit. */
interface GroupPeerLimitCallback<ToxCoreState> {
    /**
     * @param groupNumber The group number of the group for which the peer limit has changed.
     * @param peerLimit The new peer limit for the group.
     */
    fun groupPeerLimit(
        groupNumber: ToxGroupNumber,
        peerLimit: Int,
        state: ToxCoreState,
    ): ToxCoreState = state
}
