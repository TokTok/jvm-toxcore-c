package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFriendNumber

/**
 * This event is triggered when the client receives a group invite from a friend. The client must
 * store invite_data which is used to join the group via tox_group_invite_accept.
 */
interface GroupInviteCallback<ToxCoreState> {
    /**
     * @param friendNumber The friend number of the contact who sent the invite.
     * @param inviteData The invite data.
     * @param groupName The name of the group.
     */
    fun groupInvite(
        friendNumber: ToxFriendNumber,
        inviteData: ByteArray,
        groupName: ByteArray,
        state: ToxCoreState,
    ): ToxCoreState = state
}
