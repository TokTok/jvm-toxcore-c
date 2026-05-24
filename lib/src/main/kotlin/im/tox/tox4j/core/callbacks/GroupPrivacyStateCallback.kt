package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxGroupNumber
import im.tox.tox4j.core.enums.ToxGroupPrivacyState

interface GroupPrivacyStateCallback<ToxCoreState> {
    /**
     * @param groupNumber The group number of the group the privacy state is intended for.
     * @param privacyState The new privacy state.
     */
    fun groupPrivacyState(
        groupNumber: ToxGroupNumber,
        privacyState: ToxGroupPrivacyState,
        state: ToxCoreState,
    ): ToxCoreState = state
}
