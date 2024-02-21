package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.data.ToxLosslessPacket

/** This event is triggered when a custom lossless packet arrives from a friend. */
interface FriendLosslessPacketCallback<ToxCoreState> {
    /**
     * @param friendNumber The friend number of the friend who sent a lossless packet.
     * @param data A byte array containing the received packet data. The first byte is the packet id.
     */
    fun friendLosslessPacket(
        friendNumber: ToxFriendNumber,
        data: ToxLosslessPacket,
        state: ToxCoreState,
    ): ToxCoreState = state
}
