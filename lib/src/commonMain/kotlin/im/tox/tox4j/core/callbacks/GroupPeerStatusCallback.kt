package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxGroupNumber
import im.tox.tox4j.core.data.ToxGroupPeerNumber
import im.tox.tox4j.core.enums.ToxUserStatus

/** This event is triggered when a peer changes their status. */
interface GroupPeerStatusCallback<ToxCoreState> {
    /**
     * @param groupNumber The group number of the group the status change is intended for.
     * @param peerId The ID of the peer who has changed their status.
     * @param status The new status of the peer.
     */
    fun groupPeerStatus(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
        status: ToxUserStatus,
        state: ToxCoreState,
    ): ToxCoreState = state
}
