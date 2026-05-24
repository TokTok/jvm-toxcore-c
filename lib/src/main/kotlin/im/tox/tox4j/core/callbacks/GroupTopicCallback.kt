package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxGroupNumber
import im.tox.tox4j.core.data.ToxGroupPeerNumber

interface GroupTopicCallback<ToxCoreState> {
    /**
     * @param groupNumber The group number of the group the topic change is intended for.
     * @param peerId The ID of the peer who changed the topic. If the peer who set the topic is not present in our peer list this value will be set to 0.
     * @param topic The topic data.
     */
    fun groupTopic(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
        topic: ByteArray,
        state: ToxCoreState,
    ): ToxCoreState = state
}
