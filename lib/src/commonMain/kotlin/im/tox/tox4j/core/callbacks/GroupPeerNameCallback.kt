package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxGroupNumber
import im.tox.tox4j.core.data.ToxGroupPeerNumber

/** This event is triggered when a peer changes their nickname. */
interface GroupPeerNameCallback<ToxCoreState> {
    /**
     * @param groupNumber The group number of the group the name change is intended for.
     * @param peerId The ID of the peer who has changed their name.
     * @param name The name data.
     */
    fun groupPeerName(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
        name: ByteArray,
        state: ToxCoreState,
    ): ToxCoreState = state
}
