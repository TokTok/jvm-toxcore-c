package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFriendNumber

interface GroupInviteCallback<ToxCoreState> {
    /**
     * @param friendNumber The friend number of the contact who sent the invite.
     * @param inviteData The invite data.
     * @param groupName The name of the group. In conferences, this is "title".
     */
    fun groupInvite(
        friendNumber: ToxFriendNumber,
        inviteData: ByteArray,
        groupName: ByteArray,
        state: ToxCoreState,
    ): ToxCoreState = state
}
