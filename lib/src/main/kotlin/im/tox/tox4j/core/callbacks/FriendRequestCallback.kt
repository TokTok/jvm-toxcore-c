package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxPublicKey

interface FriendRequestCallback<ToxCoreState> {
    /**
     * @param publicKey The Public Key of the user who sent the friend request.
     * @param message The message they sent along with the request.
     */
    fun friendRequest(
        publicKey: ToxPublicKey,
        message: ByteArray,
        state: ToxCoreState,
    ): ToxCoreState = state
}
