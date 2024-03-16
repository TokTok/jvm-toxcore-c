package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFriendRequestMessage
import im.tox.tox4j.core.data.ToxPublicKey

/** This event is triggered when a friend request is received. */
interface FriendRequestCallback<ToxCoreState> {
    /**
     * @param publicKey The Public Key of the user who sent the friend request.
     * @param message The message they sent along with the request.
     */
    fun friendRequest(
        publicKey: ToxPublicKey,
        message: ToxFriendRequestMessage,
        state: ToxCoreState,
    ): ToxCoreState = state
}
