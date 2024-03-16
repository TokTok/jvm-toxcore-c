package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxGroupNumber
import im.tox.tox4j.core.data.ToxGroupPeerNumber

/** This event is triggered when a peer other than self joins the group. */
interface GroupPeerJoinCallback<ToxCoreState> {
    /**
     * @param groupNumber The group number of the group in which a new peer has joined.
     * @param peerId The permanent ID of the new peer. This id should not be relied on for client
     *   behaviour and should be treated as a random value.
     */
    fun groupPeerJoin(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
        state: ToxCoreState,
    ): ToxCoreState = state
}
