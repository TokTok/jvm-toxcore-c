package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxGroupNumber
import im.tox.tox4j.core.data.ToxGroupPeerNumber
import im.tox.tox4j.core.enums.ToxGroupModEvent

/**
 * This event is triggered when a moderator or founder executes a moderation event, with the
 * exception of the peer who initiates the event. It is also triggered when the observer and
 * moderator lists are silently modified (this may occur during group syncing).
 *
 * If either peer id does not designate a valid peer in the group chat, the client should manually
 * update all peer roles.
 */
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
