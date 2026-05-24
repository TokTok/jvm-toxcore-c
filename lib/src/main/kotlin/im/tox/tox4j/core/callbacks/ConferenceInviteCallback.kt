package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.enums.ToxConferenceType

interface ConferenceInviteCallback<ToxCoreState> {
    /**
     * The invitation will remain valid until the inviting friend goes offline or exits the conference.
     *
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
