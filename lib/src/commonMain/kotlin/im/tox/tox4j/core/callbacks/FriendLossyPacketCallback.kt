package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.data.ToxLossyPacket

/** This event is triggered when a lossy packet is received from a friend. */
interface FriendLossyPacketCallback<ToxCoreState> {
    /**
     * @param friendNumber The friend number of the friend who sent a lossy packet.
     * @param data A byte array containing the received packet data.
     */
    fun friendLossyPacket(
        friendNumber: ToxFriendNumber,
        data: ToxLossyPacket,
        state: ToxCoreState,
    ): ToxCoreState = state
}
