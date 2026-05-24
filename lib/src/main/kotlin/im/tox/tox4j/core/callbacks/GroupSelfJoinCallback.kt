package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxGroupNumber

interface GroupSelfJoinCallback<ToxCoreState> {
    /** @param groupNumber The group number of the group that the client has joined. */
    fun groupSelfJoin(
        groupNumber: ToxGroupNumber,
        state: ToxCoreState,
    ): ToxCoreState = state
}
