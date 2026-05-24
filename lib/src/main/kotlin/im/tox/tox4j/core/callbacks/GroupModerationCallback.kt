package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxGroupNumber
import im.tox.tox4j.core.data.ToxGroupPeerNumber
import im.tox.tox4j.core.enums.ToxGroupModEvent

interface GroupModerationCallback<ToxCoreState> {
    /**
     * @param groupNumber The group number of the group the event is intended for.
     * @param sourcePeerId The ID of the peer who initiated the event.
     * @param targetPeerId The ID of the peer who is the target of the event.
     * @param modType The type of event.
     */
    fun groupModeration(
        groupNumber: ToxGroupNumber,
        sourcePeerId: ToxGroupPeerNumber,
        targetPeerId: ToxGroupPeerNumber,
        modType: ToxGroupModEvent,
        state: ToxCoreState,
    ): ToxCoreState = state
}
