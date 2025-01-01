package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxGroupNumber
import im.tox.tox4j.core.data.ToxGroupPartMessage
import im.tox.tox4j.core.data.ToxGroupPeerNumber
import im.tox.tox4j.core.enums.ToxGroupExitType

/** This event is triggered when a peer other than self exits the group. */
interface GroupPeerExitCallback<ToxCoreState> {
    /**
     * @param groupNumber The group number of the group in which a peer has left.
     * @param peerId The ID of the peer who left the group. This ID no longer designates a valid
     *   peer and cannot be used for API calls.
     * @param exitType The type of exit event. One of ToxGroupExitType.
     * @param name The nickname of the peer who left the group.
     * @param partMessage The parting message data.
     */
    fun groupPeerExit(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
        exitType: ToxGroupExitType,
        name: ByteArray,
        partMessage: ToxGroupPartMessage,
        state: ToxCoreState,
    ): ToxCoreState = state
}
