package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxConferenceNumber

/**
 * This event is triggered when the client successfully connects to a conference after joining it
 * with the tox_conference_join function.
 */
interface ConferenceConnectedCallback<ToxCoreState> {
    /**
     * @param conferenceNumber The conference number of the conference to which we have connected.
     */
    fun conferenceConnected(
        conferenceNumber: ToxConferenceNumber,
        state: ToxCoreState,
    ): ToxCoreState = state
}
