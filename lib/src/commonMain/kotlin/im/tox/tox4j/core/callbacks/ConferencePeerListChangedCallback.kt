package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxConferenceNumber

/** This event is triggered when a peer joins or leaves the conference. */
interface ConferencePeerListChangedCallback<ToxCoreState> {
    /** @param conferenceNumber The conference number of the conference the peer is in. */
    fun conferencePeerListChanged(
        conferenceNumber: ToxConferenceNumber,
        state: ToxCoreState,
    ): ToxCoreState = state
}
