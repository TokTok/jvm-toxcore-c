package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxGroupNumber
import im.tox.tox4j.core.enums.ToxGroupTopicLock

interface GroupTopicLockCallback<ToxCoreState> {
    /**
     * @param groupNumber The group number of the group for which the topic lock has changed.
     * @param topicLock The new topic lock state.
     */
    fun groupTopicLock(
        groupNumber: ToxGroupNumber,
        topicLock: ToxGroupTopicLock,
        state: ToxCoreState,
    ): ToxCoreState = state
}
