package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxGroupNumber
import im.tox.tox4j.core.data.ToxGroupPeerNumber

/** This event is triggered when the client receives a custom packet. */
interface GroupCustomPacketCallback<ToxCoreState> {
    /**
     * @param groupNumber The group number of the group the packet is intended for.
     * @param peerId The ID of the peer who sent the packet.
     * @param data The packet data.
     */
    fun groupCustomPacket(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
        data: ByteArray,
        state: ToxCoreState,
    ): ToxCoreState = state
}
