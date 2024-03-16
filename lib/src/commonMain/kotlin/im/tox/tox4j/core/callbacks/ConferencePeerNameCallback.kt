package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxConferenceNumber
import im.tox.tox4j.core.data.ToxConferencePeerNumber

/** This event is triggered when a peer changes their name. */
interface ConferencePeerNameCallback<ToxCoreState> {
    /**
     * @param conferenceNumber The conference number of the conference the peer is in.
     * @param peerNumber The ID of the peer who changed their nickname.
     * @param name A byte array containing the new nickname.
     */
    fun conferencePeerName(
        conferenceNumber: ToxConferenceNumber,
        peerNumber: ToxConferencePeerNumber,
        name: ByteArray,
        state: ToxCoreState,
    ): ToxCoreState = state
}
