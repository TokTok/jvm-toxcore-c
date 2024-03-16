package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxGroupNumber
import im.tox.tox4j.core.enums.ToxGroupJoinFail

/** This event is triggered when the client fails to join a group. */
interface GroupJoinFailCallback<ToxCoreState> {
    /**
     * @param groupNumber The group number of the group for which the join has failed.
     * @param failType The type of group rejection.
     */
    fun groupJoinFail(
        groupNumber: ToxGroupNumber,
        failType: ToxGroupJoinFail,
        state: ToxCoreState,
    ): ToxCoreState = state
}
