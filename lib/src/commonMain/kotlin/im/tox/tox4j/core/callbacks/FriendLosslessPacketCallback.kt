package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.data.ToxLosslessPacket

/** This event is triggered when a lossless packet is received from a friend. */
interface FriendLosslessPacketCallback<ToxCoreState> {
    /**
     * @param friendNumber The friend number of the friend who sent the packet.
     * @param data A byte array containing the received packet data.
     */
    fun friendLosslessPacket(
        friendNumber: ToxFriendNumber,
        data: ToxLosslessPacket,
        state: ToxCoreState,
    ): ToxCoreState = state
}
