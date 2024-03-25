package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxConferenceNumber
import im.tox.tox4j.core.data.ToxConferencePeerNumber

/** This event is triggered when a peer changes the conference title. */
interface ConferenceTitleCallback<ToxCoreState> {
    /**
     * @param conferenceNumber The conference number of the conference the title change is intended
     *   for.
     * @param peerNumber The ID of the peer who changed the title.
     * @param title The title data.
     */
    fun conferenceTitle(
        conferenceNumber: ToxConferenceNumber,
        peerNumber: ToxConferencePeerNumber,
        title: ByteArray,
        state: ToxCoreState,
    ): ToxCoreState = state
}
