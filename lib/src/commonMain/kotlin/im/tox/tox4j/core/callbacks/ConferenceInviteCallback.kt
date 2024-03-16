package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.enums.ToxConferenceType

/** This event is triggered when the client is invited to join a conference. */
interface ConferenceInviteCallback<ToxCoreState> {
    /**
     * @param friendNumber The friend who invited us.
     * @param type The conference type (text only or audio/video).
     * @param cookie A piece of data of variable length required to join the conference.
     */
    fun conferenceInvite(
        friendNumber: ToxFriendNumber,
        type: ToxConferenceType,
        cookie: ByteArray,
        state: ToxCoreState,
    ): ToxCoreState = state
}
