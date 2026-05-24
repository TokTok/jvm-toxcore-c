package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxConferenceNumber

interface ConferencePeerListChangedCallback<ToxCoreState> {
    /** @param conferenceNumber The conference number of the conference the peer is in. */
    fun conferencePeerListChanged(
        conferenceNumber: ToxConferenceNumber,
        state: ToxCoreState,
    ): ToxCoreState = state
}
