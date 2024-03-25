package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxGroupNumber
import im.tox.tox4j.core.enums.ToxGroupVoiceState

/** This event is triggered when the group founder changes the voice state. */
interface GroupVoiceStateCallback<ToxCoreState> {
    /**
     * @param groupNumber The group number of the group the voice state change is intended for.
     * @param voiceState The new voice state.
     */
    fun groupVoiceState(
        groupNumber: ToxGroupNumber,
        voiceState: ToxGroupVoiceState,
        state: ToxCoreState,
    ): ToxCoreState = state
}
