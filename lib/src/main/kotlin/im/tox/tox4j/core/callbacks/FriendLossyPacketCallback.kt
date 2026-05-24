package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFriendNumber

interface FriendLossyPacketCallback<ToxCoreState> {
    /**
     * [callbackFriendLossyPacket] is the compatibility function to set the callback for all packet IDs except those reserved for ToxAV.
     *
     * @param friendNumber The friend number of the friend who sent a lossy packet.
     * @param data A byte array containing the received packet data.
     */
    fun friendLossyPacket(
        friendNumber: ToxFriendNumber,
        data: ByteArray,
        state: ToxCoreState,
    ): ToxCoreState = state
}
