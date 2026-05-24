package im.tox.tox4j.impl.jni

import im.tox.tox4j.core.callbacks.ToxCoreEventListener
import im.tox.tox4j.core.data.ToxConferenceNumber
import im.tox.tox4j.core.data.ToxConferencePeerNumber
import im.tox.tox4j.core.data.ToxFileNumber
import im.tox.tox4j.core.data.ToxFriendMessageId
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.data.ToxGroupMessageId
import im.tox.tox4j.core.data.ToxGroupNumber
import im.tox.tox4j.core.data.ToxGroupPeerNumber
import im.tox.tox4j.core.data.ToxPublicKey
import im.tox.tox4j.core.enums.ToxConferenceType
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.core.enums.ToxFileControl
import im.tox.tox4j.core.enums.ToxGroupExitType
import im.tox.tox4j.core.enums.ToxGroupJoinFail
import im.tox.tox4j.core.enums.ToxGroupModEvent
import im.tox.tox4j.core.enums.ToxGroupPrivacyState
import im.tox.tox4j.core.enums.ToxGroupTopicLock
import im.tox.tox4j.core.enums.ToxGroupVoiceState
import im.tox.tox4j.core.enums.ToxLogLevel
import im.tox.tox4j.core.enums.ToxMessageType
import im.tox.tox4j.core.enums.ToxUserStatus
import im.tox.tox4j.core.proto.CoreEvents

object ToxCoreEventDispatch {
    private val toxConferenceTypeValues = ToxConferenceType.values()
    private val toxConnectionValues = ToxConnection.values()
    private val toxFileControlValues = ToxFileControl.values()
    private val toxGroupExitTypeValues = ToxGroupExitType.values()
    private val toxGroupJoinFailValues = ToxGroupJoinFail.values()
    private val toxGroupModEventValues = ToxGroupModEvent.values()
    private val toxGroupPrivacyStateValues = ToxGroupPrivacyState.values()
    private val toxGroupTopicLockValues = ToxGroupTopicLock.values()
    private val toxGroupVoiceStateValues = ToxGroupVoiceState.values()
    private val toxLogLevelValues = ToxLogLevel.values()
    private val toxMessageTypeValues = ToxMessageType.values()
    private val toxUserStatusValues = ToxUserStatus.values()

    private fun <T> Array<T>.atOrFirst(index: Int): T = getOrNull(index) ?: this[0]

    fun <S> dispatch(
        handler: ToxCoreEventListener<S>,
        eventData: ByteArray?,
        state: S,
    ): S {
        if (eventData == null || eventData.isEmpty()) return state
        val events = CoreEvents.parseFrom(eventData)
        var next = state
        for (event in events.eventsList) {
            next =
                when (event.eventTypeCase) {
                    CoreEvents.Event.EventTypeCase.CONFERENCE_CONNECTED ->
                        handler.conferenceConnected(
                            ToxConferenceNumber(event.conferenceConnected.conferenceNumber),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.CONFERENCE_INVITE ->
                        handler.conferenceInvite(
                            ToxFriendNumber(event.conferenceInvite.friendNumber),
                            toxConferenceTypeValues.atOrFirst(event.conferenceInvite.type.number),
                            event.conferenceInvite.cookie.toByteArray(),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.CONFERENCE_MESSAGE ->
                        handler.conferenceMessage(
                            ToxConferenceNumber(event.conferenceMessage.conferenceNumber),
                            ToxConferencePeerNumber(event.conferenceMessage.peerNumber),
                            toxMessageTypeValues.atOrFirst(event.conferenceMessage.type.number),
                            event.conferenceMessage.message.toByteArray(),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.CONFERENCE_PEER_LIST_CHANGED ->
                        handler.conferencePeerListChanged(
                            ToxConferenceNumber(event.conferencePeerListChanged.conferenceNumber),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.CONFERENCE_PEER_NAME ->
                        handler.conferencePeerName(
                            ToxConferenceNumber(event.conferencePeerName.conferenceNumber),
                            ToxConferencePeerNumber(event.conferencePeerName.peerNumber),
                            event.conferencePeerName.name.toByteArray(),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.CONFERENCE_TITLE ->
                        handler.conferenceTitle(
                            ToxConferenceNumber(event.conferenceTitle.conferenceNumber),
                            ToxConferencePeerNumber(event.conferenceTitle.peerNumber),
                            event.conferenceTitle.title.toByteArray(),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.FILE_CHUNK_REQUEST ->
                        handler.fileChunkRequest(
                            ToxFriendNumber(event.fileChunkRequest.friendNumber),
                            ToxFileNumber(event.fileChunkRequest.fileNumber),
                            event.fileChunkRequest.position,
                            event.fileChunkRequest.length,
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.FILE_RECV ->
                        handler.fileRecv(
                            ToxFriendNumber(event.fileRecv.friendNumber),
                            ToxFileNumber(event.fileRecv.fileNumber),
                            event.fileRecv.kind,
                            event.fileRecv.fileSize,
                            event.fileRecv.filename.toByteArray(),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.FILE_RECV_CHUNK ->
                        handler.fileRecvChunk(
                            ToxFriendNumber(event.fileRecvChunk.friendNumber),
                            ToxFileNumber(event.fileRecvChunk.fileNumber),
                            event.fileRecvChunk.position,
                            event.fileRecvChunk.data.toByteArray(),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.FILE_RECV_CONTROL ->
                        handler.fileRecvControl(
                            ToxFriendNumber(event.fileRecvControl.friendNumber),
                            ToxFileNumber(event.fileRecvControl.fileNumber),
                            toxFileControlValues.atOrFirst(event.fileRecvControl.control.number),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.FRIEND_CONNECTION_STATUS ->
                        handler.friendConnectionStatus(
                            ToxFriendNumber(event.friendConnectionStatus.friendNumber),
                            toxConnectionValues.atOrFirst(event.friendConnectionStatus.connectionStatus.number),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.FRIEND_LOSSLESS_PACKET ->
                        handler.friendLosslessPacket(
                            ToxFriendNumber(event.friendLosslessPacket.friendNumber),
                            event.friendLosslessPacket.data.toByteArray(),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.FRIEND_LOSSY_PACKET ->
                        handler.friendLossyPacket(
                            ToxFriendNumber(event.friendLossyPacket.friendNumber),
                            event.friendLossyPacket.data.toByteArray(),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.FRIEND_MESSAGE ->
                        handler.friendMessage(
                            ToxFriendNumber(event.friendMessage.friendNumber),
                            toxMessageTypeValues.atOrFirst(event.friendMessage.type.number),
                            event.friendMessage.message.toByteArray(),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.FRIEND_NAME ->
                        handler.friendName(
                            ToxFriendNumber(event.friendName.friendNumber),
                            event.friendName.name.toByteArray(),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.FRIEND_READ_RECEIPT ->
                        handler.friendReadReceipt(
                            ToxFriendNumber(event.friendReadReceipt.friendNumber),
                            ToxFriendMessageId(event.friendReadReceipt.messageId),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.FRIEND_REQUEST ->
                        handler.friendRequest(
                            ToxPublicKey(event.friendRequest.publicKey.toByteArray()),
                            event.friendRequest.message.toByteArray(),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.FRIEND_STATUS ->
                        handler.friendStatus(
                            ToxFriendNumber(event.friendStatus.friendNumber),
                            toxUserStatusValues.atOrFirst(event.friendStatus.status.number),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.FRIEND_STATUS_MESSAGE ->
                        handler.friendStatusMessage(
                            ToxFriendNumber(event.friendStatusMessage.friendNumber),
                            event.friendStatusMessage.message.toByteArray(),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.FRIEND_TYPING ->
                        handler.friendTyping(
                            ToxFriendNumber(event.friendTyping.friendNumber),
                            event.friendTyping.typing,
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.GROUP_CUSTOM_PACKET ->
                        handler.groupCustomPacket(
                            ToxGroupNumber(event.groupCustomPacket.groupNumber),
                            ToxGroupPeerNumber(event.groupCustomPacket.peerId),
                            event.groupCustomPacket.data.toByteArray(),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.GROUP_CUSTOM_PRIVATE_PACKET ->
                        handler.groupCustomPrivatePacket(
                            ToxGroupNumber(event.groupCustomPrivatePacket.groupNumber),
                            ToxGroupPeerNumber(event.groupCustomPrivatePacket.peerId),
                            event.groupCustomPrivatePacket.data.toByteArray(),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.GROUP_INVITE ->
                        handler.groupInvite(
                            ToxFriendNumber(event.groupInvite.friendNumber),
                            event.groupInvite.inviteData.toByteArray(),
                            event.groupInvite.groupName.toByteArray(),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.GROUP_JOIN_FAIL ->
                        handler.groupJoinFail(
                            ToxGroupNumber(event.groupJoinFail.groupNumber),
                            toxGroupJoinFailValues.atOrFirst(event.groupJoinFail.failType.number),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.GROUP_MESSAGE ->
                        handler.groupMessage(
                            ToxGroupNumber(event.groupMessage.groupNumber),
                            ToxGroupPeerNumber(event.groupMessage.peerId),
                            toxMessageTypeValues.atOrFirst(event.groupMessage.messageType.number),
                            event.groupMessage.message.toByteArray(),
                            ToxGroupMessageId(event.groupMessage.messageId),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.GROUP_MODERATION ->
                        handler.groupModeration(
                            ToxGroupNumber(event.groupModeration.groupNumber),
                            ToxGroupPeerNumber(event.groupModeration.sourcePeerId),
                            ToxGroupPeerNumber(event.groupModeration.targetPeerId),
                            toxGroupModEventValues.atOrFirst(event.groupModeration.modType.number),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.GROUP_PASSWORD ->
                        handler.groupPassword(
                            ToxGroupNumber(event.groupPassword.groupNumber),
                            event.groupPassword.password.toByteArray(),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.GROUP_PEER_EXIT ->
                        handler.groupPeerExit(
                            ToxGroupNumber(event.groupPeerExit.groupNumber),
                            ToxGroupPeerNumber(event.groupPeerExit.peerId),
                            toxGroupExitTypeValues.atOrFirst(event.groupPeerExit.exitType.number),
                            event.groupPeerExit.name.toByteArray(),
                            event.groupPeerExit.partMessage.toByteArray(),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.GROUP_PEER_JOIN ->
                        handler.groupPeerJoin(
                            ToxGroupNumber(event.groupPeerJoin.groupNumber),
                            ToxGroupPeerNumber(event.groupPeerJoin.peerId),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.GROUP_PEER_LIMIT ->
                        handler.groupPeerLimit(
                            ToxGroupNumber(event.groupPeerLimit.groupNumber),
                            event.groupPeerLimit.peerLimit,
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.GROUP_PEER_NAME ->
                        handler.groupPeerName(
                            ToxGroupNumber(event.groupPeerName.groupNumber),
                            ToxGroupPeerNumber(event.groupPeerName.peerId),
                            event.groupPeerName.name.toByteArray(),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.GROUP_PEER_STATUS ->
                        handler.groupPeerStatus(
                            ToxGroupNumber(event.groupPeerStatus.groupNumber),
                            ToxGroupPeerNumber(event.groupPeerStatus.peerId),
                            toxUserStatusValues.atOrFirst(event.groupPeerStatus.status.number),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.GROUP_PRIVACY_STATE ->
                        handler.groupPrivacyState(
                            ToxGroupNumber(event.groupPrivacyState.groupNumber),
                            toxGroupPrivacyStateValues.atOrFirst(event.groupPrivacyState.privacyState.number),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.GROUP_PRIVATE_MESSAGE ->
                        handler.groupPrivateMessage(
                            ToxGroupNumber(event.groupPrivateMessage.groupNumber),
                            ToxGroupPeerNumber(event.groupPrivateMessage.peerId),
                            toxMessageTypeValues.atOrFirst(event.groupPrivateMessage.messageType.number),
                            event.groupPrivateMessage.message.toByteArray(),
                            ToxGroupMessageId(event.groupPrivateMessage.messageId),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.GROUP_SELF_JOIN ->
                        handler.groupSelfJoin(
                            ToxGroupNumber(event.groupSelfJoin.groupNumber),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.GROUP_TOPIC ->
                        handler.groupTopic(
                            ToxGroupNumber(event.groupTopic.groupNumber),
                            ToxGroupPeerNumber(event.groupTopic.peerId),
                            event.groupTopic.topic.toByteArray(),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.GROUP_TOPIC_LOCK ->
                        handler.groupTopicLock(
                            ToxGroupNumber(event.groupTopicLock.groupNumber),
                            toxGroupTopicLockValues.atOrFirst(event.groupTopicLock.topicLock.number),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.GROUP_VOICE_STATE ->
                        handler.groupVoiceState(
                            ToxGroupNumber(event.groupVoiceState.groupNumber),
                            toxGroupVoiceStateValues.atOrFirst(event.groupVoiceState.voiceState.number),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.LOG ->
                        handler.log(
                            toxLogLevelValues.atOrFirst(event.log.level.number),
                            event.log.file,
                            event.log.line,
                            event.log.func,
                            event.log.message,
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.SELF_CONNECTION_STATUS ->
                        handler.selfConnectionStatus(
                            toxConnectionValues.atOrFirst(event.selfConnectionStatus.connectionStatus.number),
                            next,
                        )
                    CoreEvents.Event.EventTypeCase.EVENTTYPE_NOT_SET -> next
                }
        }
        return next
    }
}
