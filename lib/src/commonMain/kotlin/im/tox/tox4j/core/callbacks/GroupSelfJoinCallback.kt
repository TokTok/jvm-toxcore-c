package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxGroupNumber

/**
 * This event is triggered when the client has successfully joined a group. Use this to initialize
 * any group information the client may need.
 */
interface GroupSelfJoinCallback<ToxCoreState> {
    /** @param groupNumber The group number of the group that the client has joined. */
    fun groupSelfJoin(
        groupNumber: ToxGroupNumber,
        state: ToxCoreState,
    ): ToxCoreState = state
}
