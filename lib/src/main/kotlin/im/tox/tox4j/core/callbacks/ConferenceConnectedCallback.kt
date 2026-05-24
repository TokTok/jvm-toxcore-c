package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxConferenceNumber

interface ConferenceConnectedCallback<ToxCoreState> {
    /** @param conferenceNumber The conference number of the conference to which we have connected. */
    fun conferenceConnected(
        conferenceNumber: ToxConferenceNumber,
        state: ToxCoreState,
    ): ToxCoreState = state
}
