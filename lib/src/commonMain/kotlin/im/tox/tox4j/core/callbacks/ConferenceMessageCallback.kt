package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxConferenceNumber
import im.tox.tox4j.core.data.ToxConferencePeerNumber
import im.tox.tox4j.core.enums.ToxMessageType

/** This event is triggered when the client receives a conference message. */
interface ConferenceMessageCallback<ToxCoreState> {
    /**
     * @param conferenceNumber The conference number of the conference the message is intended for.
     * @param peerNumber The ID of the peer who sent the message.
     * @param type The type of message (normal, action, ...).
     * @param message The message data.
     */
    fun conferenceMessage(
        conferenceNumber: ToxConferenceNumber,
        peerNumber: ToxConferencePeerNumber,
        type: ToxMessageType,
        message: ByteArray,
        state: ToxCoreState,
    ): ToxCoreState = state
}
