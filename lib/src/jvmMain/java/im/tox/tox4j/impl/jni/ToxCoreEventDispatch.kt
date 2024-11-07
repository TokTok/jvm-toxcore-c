package im.tox.tox4j.impl.jni

import im.tox.tox4j.core.callbacks.ConferenceConnectedCallback
import im.tox.tox4j.core.callbacks.ConferenceInviteCallback
import im.tox.tox4j.core.callbacks.ConferenceMessageCallback
import im.tox.tox4j.core.callbacks.ConferencePeerListChangedCallback
import im.tox.tox4j.core.callbacks.ConferencePeerNameCallback
import im.tox.tox4j.core.callbacks.ConferenceTitleCallback
import im.tox.tox4j.core.callbacks.FileChunkRequestCallback
import im.tox.tox4j.core.callbacks.FileRecvCallback
import im.tox.tox4j.core.callbacks.FileRecvChunkCallback
import im.tox.tox4j.core.callbacks.FileRecvControlCallback
import im.tox.tox4j.core.callbacks.FriendConnectionStatusCallback
import im.tox.tox4j.core.callbacks.FriendLosslessPacketCallback
import im.tox.tox4j.core.callbacks.FriendLossyPacketCallback
import im.tox.tox4j.core.callbacks.FriendMessageCallback
import im.tox.tox4j.core.callbacks.FriendNameCallback
import im.tox.tox4j.core.callbacks.FriendReadReceiptCallback
import im.tox.tox4j.core.callbacks.FriendRequestCallback
import im.tox.tox4j.core.callbacks.FriendStatusCallback
import im.tox.tox4j.core.callbacks.FriendStatusMessageCallback
import im.tox.tox4j.core.callbacks.FriendTypingCallback
import im.tox.tox4j.core.callbacks.GroupCustomPacketCallback
import im.tox.tox4j.core.callbacks.GroupCustomPrivatePacketCallback
import im.tox.tox4j.core.callbacks.GroupInviteCallback
import im.tox.tox4j.core.callbacks.GroupJoinFailCallback
import im.tox.tox4j.core.callbacks.GroupMessageCallback
import im.tox.tox4j.core.callbacks.GroupModerationCallback
import im.tox.tox4j.core.callbacks.GroupPasswordCallback
import im.tox.tox4j.core.callbacks.GroupPeerExitCallback
import im.tox.tox4j.core.callbacks.GroupPeerJoinCallback
import im.tox.tox4j.core.callbacks.GroupPeerLimitCallback
import im.tox.tox4j.core.callbacks.GroupPeerNameCallback
import im.tox.tox4j.core.callbacks.GroupPeerStatusCallback
import im.tox.tox4j.core.callbacks.GroupPrivacyStateCallback
import im.tox.tox4j.core.callbacks.GroupPrivateMessageCallback
import im.tox.tox4j.core.callbacks.GroupSelfJoinCallback
import im.tox.tox4j.core.callbacks.GroupTopicCallback
import im.tox.tox4j.core.callbacks.GroupTopicLockCallback
import im.tox.tox4j.core.callbacks.GroupVoiceStateCallback
import im.tox.tox4j.core.callbacks.SelfConnectionStatusCallback
import im.tox.tox4j.core.callbacks.ToxCoreEventListener
import im.tox.tox4j.core.data.ToxConferenceNumber
import im.tox.tox4j.core.data.ToxConferencePeerNumber
import im.tox.tox4j.core.data.ToxFilename
import im.tox.tox4j.core.data.ToxFriendMessage
import im.tox.tox4j.core.data.ToxFriendMessageId
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.data.ToxFriendRequestMessage
import im.tox.tox4j.core.data.ToxGroupMessageId
import im.tox.tox4j.core.data.ToxGroupNumber
import im.tox.tox4j.core.data.ToxGroupPartMessage
import im.tox.tox4j.core.data.ToxGroupPassword
import im.tox.tox4j.core.data.ToxGroupPeerNumber
import im.tox.tox4j.core.data.ToxGroupTopic
import im.tox.tox4j.core.data.ToxLosslessPacket
import im.tox.tox4j.core.data.ToxLossyPacket
import im.tox.tox4j.core.data.ToxNickname
import im.tox.tox4j.core.data.ToxPublicKey
import im.tox.tox4j.core.data.ToxStatusMessage
import im.tox.tox4j.core.enums.ToxConferenceType
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.core.enums.ToxFileControl
import im.tox.tox4j.core.enums.ToxGroupExitType
import im.tox.tox4j.core.enums.ToxGroupJoinFail
import im.tox.tox4j.core.enums.ToxGroupModEvent
import im.tox.tox4j.core.enums.ToxGroupPrivacyState
import im.tox.tox4j.core.enums.ToxGroupTopicLock
import im.tox.tox4j.core.enums.ToxGroupVoiceState
import im.tox.tox4j.core.enums.ToxMessageType
import im.tox.tox4j.core.enums.ToxUserStatus
import im.tox.tox4j.core.proto.ConferenceConnected
import im.tox.tox4j.core.proto.ConferenceInvite
import im.tox.tox4j.core.proto.ConferenceMessage
import im.tox.tox4j.core.proto.ConferencePeerListChanged
import im.tox.tox4j.core.proto.ConferencePeerName
import im.tox.tox4j.core.proto.ConferenceTitle
import im.tox.tox4j.core.proto.ConferenceType
import im.tox.tox4j.core.proto.Connection
import im.tox.tox4j.core.proto.CoreEvents
import im.tox.tox4j.core.proto.FileChunkRequest
import im.tox.tox4j.core.proto.FileControl
import im.tox.tox4j.core.proto.FileRecv
import im.tox.tox4j.core.proto.FileRecvChunk
import im.tox.tox4j.core.proto.FileRecvControl
import im.tox.tox4j.core.proto.FriendConnectionStatus
import im.tox.tox4j.core.proto.FriendLosslessPacket
import im.tox.tox4j.core.proto.FriendLossyPacket
import im.tox.tox4j.core.proto.FriendMessage
import im.tox.tox4j.core.proto.FriendName
import im.tox.tox4j.core.proto.FriendReadReceipt
import im.tox.tox4j.core.proto.FriendRequest
import im.tox.tox4j.core.proto.FriendStatus
import im.tox.tox4j.core.proto.FriendStatusMessage
import im.tox.tox4j.core.proto.FriendTyping
import im.tox.tox4j.core.proto.GroupCustomPacket
import im.tox.tox4j.core.proto.GroupCustomPrivatePacket
import im.tox.tox4j.core.proto.GroupExitType
import im.tox.tox4j.core.proto.GroupInvite
import im.tox.tox4j.core.proto.GroupJoinFail
import im.tox.tox4j.core.proto.GroupMessage
import im.tox.tox4j.core.proto.GroupModEvent
import im.tox.tox4j.core.proto.GroupModeration
import im.tox.tox4j.core.proto.GroupPassword
import im.tox.tox4j.core.proto.GroupPeerExit
import im.tox.tox4j.core.proto.GroupPeerJoin
import im.tox.tox4j.core.proto.GroupPeerLimit
import im.tox.tox4j.core.proto.GroupPeerName
import im.tox.tox4j.core.proto.GroupPeerStatus
import im.tox.tox4j.core.proto.GroupPrivacyState
import im.tox.tox4j.core.proto.GroupPrivateMessage
import im.tox.tox4j.core.proto.GroupSelfJoin
import im.tox.tox4j.core.proto.GroupTopic
import im.tox.tox4j.core.proto.GroupTopicLock
import im.tox.tox4j.core.proto.GroupVoiceState
import im.tox.tox4j.core.proto.MessageType
import im.tox.tox4j.core.proto.SelfConnectionStatus
import im.tox.tox4j.core.proto.UserStatus

object ToxCoreEventDispatch {
    private fun convert(status: Connection.Type): ToxConnection =
        when (status) {
            Connection.Type.NONE -> ToxConnection.NONE
            Connection.Type.TCP -> ToxConnection.TCP
            Connection.Type.UDP -> ToxConnection.UDP
            Connection.Type.UNRECOGNIZED -> ToxConnection.NONE
        }

    private fun convert(status: UserStatus.Type): ToxUserStatus =
        when (status) {
            UserStatus.Type.NONE -> ToxUserStatus.NONE
            UserStatus.Type.AWAY -> ToxUserStatus.AWAY
            UserStatus.Type.BUSY -> ToxUserStatus.BUSY
            UserStatus.Type.UNRECOGNIZED -> ToxUserStatus.NONE
        }

    private fun convert(status: ToxUserStatus): UserStatus.Type =
        when (status) {
            ToxUserStatus.NONE -> UserStatus.Type.NONE
            ToxUserStatus.AWAY -> UserStatus.Type.AWAY
            ToxUserStatus.BUSY -> UserStatus.Type.BUSY
        }

    private fun convert(control: FileControl.Type): ToxFileControl =
        when (control) {
            FileControl.Type.RESUME -> ToxFileControl.RESUME
            FileControl.Type.PAUSE -> ToxFileControl.PAUSE
            FileControl.Type.CANCEL -> ToxFileControl.CANCEL
            FileControl.Type.UNRECOGNIZED -> ToxFileControl.CANCEL
        }

    private fun convert(messageType: MessageType.Type): ToxMessageType =
        when (messageType) {
            MessageType.Type.NORMAL -> ToxMessageType.NORMAL
            MessageType.Type.ACTION -> ToxMessageType.ACTION
            MessageType.Type.UNRECOGNIZED -> ToxMessageType.NORMAL
        }

    private fun convert(conferenceType: ConferenceType.Type): ToxConferenceType =
        when (conferenceType) {
            ConferenceType.Type.TEXT -> ToxConferenceType.TEXT
            ConferenceType.Type.AV -> ToxConferenceType.AV
            ConferenceType.Type.UNRECOGNIZED -> ToxConferenceType.TEXT
        }

    private fun convert(groupExitType: GroupExitType.Type): ToxGroupExitType =
        when (groupExitType) {
            GroupExitType.Type.QUIT -> ToxGroupExitType.QUIT
            GroupExitType.Type.TIMEOUT -> ToxGroupExitType.TIMEOUT
            GroupExitType.Type.DISCONNECTED -> ToxGroupExitType.DISCONNECTED
            GroupExitType.Type.SELF_DISCONNECTED -> ToxGroupExitType.SELF_DISCONNECTED
            GroupExitType.Type.KICK -> ToxGroupExitType.KICK
            GroupExitType.Type.SYNC_ERROR -> ToxGroupExitType.SYNC_ERROR
            GroupExitType.Type.UNRECOGNIZED -> ToxGroupExitType.QUIT
        }

    private fun convert(groupModEvent: GroupModEvent.Type): ToxGroupModEvent =
        when (groupModEvent) {
            GroupModEvent.Type.KICK -> ToxGroupModEvent.KICK
            GroupModEvent.Type.OBSERVER -> ToxGroupModEvent.OBSERVER
            GroupModEvent.Type.USER -> ToxGroupModEvent.USER
            GroupModEvent.Type.MODERATOR -> ToxGroupModEvent.MODERATOR
            GroupModEvent.Type.UNRECOGNIZED -> ToxGroupModEvent.KICK
        }

    private fun convert(groupPrivacyState: GroupPrivacyState.Type): ToxGroupPrivacyState =
        when (groupPrivacyState) {
            GroupPrivacyState.Type.PUBLIC -> ToxGroupPrivacyState.PUBLIC
            GroupPrivacyState.Type.PRIVATE -> ToxGroupPrivacyState.PRIVATE
            GroupPrivacyState.Type.UNRECOGNIZED -> ToxGroupPrivacyState.PUBLIC
        }

    private fun convert(groupVoiceState: GroupVoiceState.Type): ToxGroupVoiceState =
        when (groupVoiceState) {
            GroupVoiceState.Type.ALL -> ToxGroupVoiceState.ALL
            GroupVoiceState.Type.MODERATOR -> ToxGroupVoiceState.MODERATOR
            GroupVoiceState.Type.FOUNDER -> ToxGroupVoiceState.FOUNDER
            GroupVoiceState.Type.UNRECOGNIZED -> ToxGroupVoiceState.ALL
        }

    private fun convert(groupTopicLock: GroupTopicLock.Type): ToxGroupTopicLock =
        when (groupTopicLock) {
            GroupTopicLock.Type.ENABLED -> ToxGroupTopicLock.ENABLED
            GroupTopicLock.Type.DISABLED -> ToxGroupTopicLock.DISABLED
            GroupTopicLock.Type.UNRECOGNIZED -> ToxGroupTopicLock.ENABLED
        }

    private fun convert(groupJoinFail: GroupJoinFail.Type): ToxGroupJoinFail =
        when (groupJoinFail) {
            GroupJoinFail.Type.PEER_LIMIT -> ToxGroupJoinFail.PEER_LIMIT
            GroupJoinFail.Type.INVALID_PASSWORD -> ToxGroupJoinFail.INVALID_PASSWORD
            GroupJoinFail.Type.UNKNOWN -> ToxGroupJoinFail.UNKNOWN
            GroupJoinFail.Type.UNRECOGNIZED -> ToxGroupJoinFail.UNKNOWN
        }

    private fun <S> dispatchSelfConnectionStatus(
        handler: SelfConnectionStatusCallback<S>,
        ev: SelfConnectionStatus,
        state: S,
    ): S = handler.selfConnectionStatus(convert(ev.getConnectionStatus()), state)

    private fun <S> dispatchFriendName(
        handler: FriendNameCallback<S>,
        ev: FriendName,
        state: S,
    ): S =
        handler.friendName(
            ToxFriendNumber(ev.getFriendNumber()),
            ToxNickname(ev.getName().toByteArray()),
            state,
        )

    private fun <S> dispatchFriendStatusMessage(
        handler: FriendStatusMessageCallback<S>,
        ev: FriendStatusMessage,
        state: S,
    ): S =
        handler.friendStatusMessage(
            ToxFriendNumber(ev.getFriendNumber()),
            ToxStatusMessage(ev.getMessage().toByteArray()),
            state,
        )

    private fun <S> dispatchFriendStatus(
        handler: FriendStatusCallback<S>,
        ev: FriendStatus,
        state: S,
    ): S =
        handler.friendStatus(
            ToxFriendNumber(ev.getFriendNumber()),
            convert(ev.getStatus()),
            state,
        )

    private fun <S> dispatchFriendConnectionStatus(
        handler: FriendConnectionStatusCallback<S>,
        ev: FriendConnectionStatus,
        state: S,
    ): S =
        handler.friendConnectionStatus(
            ToxFriendNumber(ev.getFriendNumber()),
            convert(ev.getConnectionStatus()),
            state,
        )

    private fun <S> dispatchFriendTyping(
        handler: FriendTypingCallback<S>,
        ev: FriendTyping,
        state: S,
    ): S =
        handler.friendTyping(
            ToxFriendNumber(ev.getFriendNumber()),
            ev.getIsTyping(),
            state,
        )

    private fun <S> dispatchFriendReadReceipt(
        handler: FriendReadReceiptCallback<S>,
        ev: FriendReadReceipt,
        state: S,
    ): S =
        handler.friendReadReceipt(
            ToxFriendNumber(ev.getFriendNumber()),
            ToxFriendMessageId(ev.getMessageId()),
            state,
        )

    private fun <S> dispatchFriendRequest(
        handler: FriendRequestCallback<S>,
        ev: FriendRequest,
        state: S,
    ): S =
        handler.friendRequest(
            ToxPublicKey(ev.getPublicKey().toByteArray()),
            ToxFriendRequestMessage(ev.getMessage().toByteArray()),
            state,
        )

    private fun <S> dispatchFriendMessage(
        handler: FriendMessageCallback<S>,
        ev: FriendMessage,
        state: S,
    ): S =
        handler.friendMessage(
            ToxFriendNumber(ev.getFriendNumber()),
            convert(ev.getMessageType()),
            ToxFriendMessage(ev.getMessage().toByteArray()),
            state,
        )

    private fun <S> dispatchFileRecvControl(
        handler: FileRecvControlCallback<S>,
        ev: FileRecvControl,
        state: S,
    ): S =
        handler.fileRecvControl(
            ToxFriendNumber(ev.getFriendNumber()),
            ev.getFileNumber(),
            convert(ev.getControl()),
            state,
        )

    private fun <S> dispatchFileChunkRequest(
        handler: FileChunkRequestCallback<S>,
        ev: FileChunkRequest,
        state: S,
    ): S =
        handler.fileChunkRequest(
            ToxFriendNumber(ev.getFriendNumber()),
            ev.getFileNumber(),
            ev.getPosition(),
            ev.getLength(),
            state,
        )

    private fun <S> dispatchFileRecv(
        handler: FileRecvCallback<S>,
        ev: FileRecv,
        state: S,
    ): S =
        handler.fileRecv(
            ToxFriendNumber(ev.getFriendNumber()),
            ev.getFileNumber(),
            ev.getKind(),
            ev.getFileSize(),
            ToxFilename(ev.getFilename().toByteArray()),
            state,
        )

    private fun <S> dispatchFileRecvChunk(
        handler: FileRecvChunkCallback<S>,
        ev: FileRecvChunk,
        state: S,
    ): S =
        handler.fileRecvChunk(
            ToxFriendNumber(ev.getFriendNumber()),
            ev.getFileNumber(),
            ev.getPosition(),
            ev.getData().toByteArray(),
            state,
        )

    private fun <S> dispatchFriendLossyPacket(
        handler: FriendLossyPacketCallback<S>,
        ev: FriendLossyPacket,
        state: S,
    ): S =
        handler.friendLossyPacket(
            ToxFriendNumber(ev.getFriendNumber()),
            ToxLossyPacket(ev.getData().toByteArray()),
            state,
        )

    private fun <S> dispatchFriendLosslessPacket(
        handler: FriendLosslessPacketCallback<S>,
        ev: FriendLosslessPacket,
        state: S,
    ): S =
        handler.friendLosslessPacket(
            ToxFriendNumber(ev.getFriendNumber()),
            ToxLosslessPacket(ev.getData().toByteArray()),
            state,
        )

    private fun <S> dispatchConferenceConnected(
        handler: ConferenceConnectedCallback<S>,
        ev: ConferenceConnected,
        state: S,
    ): S =
        handler.conferenceConnected(
            ToxConferenceNumber(ev.getConferenceNumber()),
            state,
        )

    private fun <S> dispatchConferenceInvite(
        handler: ConferenceInviteCallback<S>,
        ev: ConferenceInvite,
        state: S,
    ): S =
        handler.conferenceInvite(
            ToxFriendNumber(ev.getFriendNumber()),
            convert(ev.getType()),
            ev.getCookie().toByteArray(),
            state,
        )

    private fun <S> dispatchConferenceMessage(
        handler: ConferenceMessageCallback<S>,
        ev: ConferenceMessage,
        state: S,
    ): S =
        handler.conferenceMessage(
            ToxConferenceNumber(ev.getConferenceNumber()),
            ToxConferencePeerNumber(ev.getPeerNumber()),
            convert(ev.getMessageType()),
            ev.getMessage().toByteArray(),
            state,
        )

    private fun <S> dispatchConferencePeerListChanged(
        handler: ConferencePeerListChangedCallback<S>,
        ev: ConferencePeerListChanged,
        state: S,
    ): S =
        handler.conferencePeerListChanged(
            ToxConferenceNumber(ev.getConferenceNumber()),
            state,
        )

    private fun <S> dispatchConferencePeerName(
        handler: ConferencePeerNameCallback<S>,
        ev: ConferencePeerName,
        state: S,
    ): S =
        handler.conferencePeerName(
            ToxConferenceNumber(ev.getConferenceNumber()),
            ToxConferencePeerNumber(ev.getPeerNumber()),
            ev.getName().toByteArray(),
            state,
        )

    private fun <S> dispatchConferenceTitle(
        handler: ConferenceTitleCallback<S>,
        ev: ConferenceTitle,
        state: S,
    ): S =
        handler.conferenceTitle(
            ToxConferenceNumber(ev.getConferenceNumber()),
            ToxConferencePeerNumber(ev.getPeerNumber()),
            ev.getTitle().toByteArray(),
            state,
        )

    private fun <S> dispatchGroupCustomPacket(
        handler: GroupCustomPacketCallback<S>,
        ev: GroupCustomPacket,
        state: S,
    ): S =
        handler.groupCustomPacket(
            ToxGroupNumber(ev.getGroupNumber()),
            ToxGroupPeerNumber(ev.getPeerId()),
            ev.getData().toByteArray(),
            state,
        )

    private fun <S> dispatchGroupCustomPrivatePacket(
        handler: GroupCustomPrivatePacketCallback<S>,
        ev: GroupCustomPrivatePacket,
        state: S,
    ): S =
        handler.groupCustomPrivatePacket(
            ToxGroupNumber(ev.getGroupNumber()),
            ToxGroupPeerNumber(ev.getPeerId()),
            ev.getData().toByteArray(),
            state,
        )

    private fun <S> dispatchGroupInvite(
        handler: GroupInviteCallback<S>,
        ev: GroupInvite,
        state: S,
    ): S =
        handler.groupInvite(
            ToxFriendNumber(ev.getFriendNumber()),
            ev.getInviteData().toByteArray(),
            ev.getGroupName().toByteArray(),
            state,
        )

    private fun <S> dispatchGroupJoinFail(
        handler: GroupJoinFailCallback<S>,
        ev: GroupJoinFail,
        state: S,
    ): S =
        handler.groupJoinFail(
            ToxGroupNumber(ev.getGroupNumber()),
            convert(ev.getFailType()),
            state,
        )

    private fun <S> dispatchGroupMessage(
        handler: GroupMessageCallback<S>,
        ev: GroupMessage,
        state: S,
    ): S =
        handler.groupMessage(
            ToxGroupNumber(ev.getGroupNumber()),
            ToxGroupPeerNumber(ev.getPeerId()),
            convert(ev.getMessageType()),
            ev.getMessage().toByteArray(),
            ToxGroupMessageId(ev.getMessageId()),
            state,
        )

    private fun <S> dispatchGroupModeration(
        handler: GroupModerationCallback<S>,
        ev: GroupModeration,
        state: S,
    ): S =
        handler.groupModeration(
            ToxGroupNumber(ev.getGroupNumber()),
            ToxGroupPeerNumber(ev.getSourcePeerId()),
            ToxGroupPeerNumber(ev.getTargetPeerId()),
            convert(ev.getModType()),
            state,
        )

    private fun <S> dispatchGroupPassword(
        handler: GroupPasswordCallback<S>,
        ev: GroupPassword,
        state: S,
    ): S =
        handler.groupPassword(
            ToxGroupNumber(ev.getGroupNumber()),
            ToxGroupPassword(ev.getPassword().toByteArray()),
            state,
        )

    private fun <S> dispatchGroupPeerExit(
        handler: GroupPeerExitCallback<S>,
        ev: GroupPeerExit,
        state: S,
    ): S =
        handler.groupPeerExit(
            ToxGroupNumber(ev.getGroupNumber()),
            ToxGroupPeerNumber(ev.getPeerId()),
            convert(ev.getExitType()),
            ev.getName().toByteArray(),
            ToxGroupPartMessage(ev.getPartMessage().toByteArray()),
            state,
        )

    private fun <S> dispatchGroupPeerJoin(
        handler: GroupPeerJoinCallback<S>,
        ev: GroupPeerJoin,
        state: S,
    ): S =
        handler.groupPeerJoin(
            ToxGroupNumber(ev.getGroupNumber()),
            ToxGroupPeerNumber(ev.getPeerId()),
            state,
        )

    private fun <S> dispatchGroupPeerLimit(
        handler: GroupPeerLimitCallback<S>,
        ev: GroupPeerLimit,
        state: S,
    ): S =
        handler.groupPeerLimit(
            ToxGroupNumber(ev.getGroupNumber()),
            ev.getPeerLimit(),
            state,
        )

    private fun <S> dispatchGroupPeerName(
        handler: GroupPeerNameCallback<S>,
        ev: GroupPeerName,
        state: S,
    ): S =
        handler.groupPeerName(
            ToxGroupNumber(ev.getGroupNumber()),
            ToxGroupPeerNumber(ev.getPeerId()),
            ev.getName().toByteArray(),
            state,
        )

    private fun <S> dispatchGroupPeerStatus(
        handler: GroupPeerStatusCallback<S>,
        ev: GroupPeerStatus,
        state: S,
    ): S =
        handler.groupPeerStatus(
            ToxGroupNumber(ev.getGroupNumber()),
            ToxGroupPeerNumber(ev.getPeerId()),
            convert(ev.getStatus()),
            state,
        )

    private fun <S> dispatchGroupPrivacyState(
        handler: GroupPrivacyStateCallback<S>,
        ev: GroupPrivacyState,
        state: S,
    ): S =
        handler.groupPrivacyState(
            ToxGroupNumber(ev.getGroupNumber()),
            convert(ev.getPrivacyState()),
            state,
        )

    private fun <S> dispatchGroupPrivateMessage(
        handler: GroupPrivateMessageCallback<S>,
        ev: GroupPrivateMessage,
        state: S,
    ): S =
        handler.groupPrivateMessage(
            ToxGroupNumber(ev.getGroupNumber()),
            ToxGroupPeerNumber(ev.getPeerId()),
            convert(ev.getMessageType()),
            ev.getMessage().toByteArray(),
            ToxGroupMessageId(ev.getMessageId()),
            state,
        )

    private fun <S> dispatchGroupSelfJoin(
        handler: GroupSelfJoinCallback<S>,
        ev: GroupSelfJoin,
        state: S,
    ): S =
        handler.groupSelfJoin(
            ToxGroupNumber(ev.getGroupNumber()),
            state,
        )

    private fun <S> dispatchGroupTopic(
        handler: GroupTopicCallback<S>,
        ev: GroupTopic,
        state: S,
    ): S =
        handler.groupTopic(
            ToxGroupNumber(ev.getGroupNumber()),
            ToxGroupPeerNumber(ev.getPeerId()),
            ToxGroupTopic(ev.getTopic().toByteArray()),
            state,
        )

    private fun <S> dispatchGroupTopicLock(
        handler: GroupTopicLockCallback<S>,
        ev: GroupTopicLock,
        state: S,
    ): S =
        handler.groupTopicLock(
            ToxGroupNumber(ev.getGroupNumber()),
            convert(ev.getTopicLock()),
            state,
        )

    private fun <S> dispatchGroupVoiceState(
        handler: GroupVoiceStateCallback<S>,
        ev: GroupVoiceState,
        state: S,
    ): S =
        handler.groupVoiceState(
            ToxGroupNumber(ev.getGroupNumber()),
            convert(ev.getVoiceState()),
            state,
        )

    private fun <S> dispatchEvent(
        handler: ToxCoreEventListener<S>,
        event: CoreEvents.Event,
        state: S,
    ): S =
        when (event.getEventTypeCase()) {
            CoreEvents.Event.EventTypeCase.SELF_CONNECTION_STATUS ->
                dispatchSelfConnectionStatus(handler, event.getSelfConnectionStatus(), state)
            CoreEvents.Event.EventTypeCase.FRIEND_NAME ->
                dispatchFriendName(handler, event.getFriendName(), state)
            CoreEvents.Event.EventTypeCase.FRIEND_STATUS_MESSAGE ->
                dispatchFriendStatusMessage(handler, event.getFriendStatusMessage(), state)
            CoreEvents.Event.EventTypeCase.FRIEND_STATUS ->
                dispatchFriendStatus(handler, event.getFriendStatus(), state)
            CoreEvents.Event.EventTypeCase.FRIEND_CONNECTION_STATUS ->
                dispatchFriendConnectionStatus(handler, event.getFriendConnectionStatus(), state)
            CoreEvents.Event.EventTypeCase.FRIEND_TYPING ->
                dispatchFriendTyping(handler, event.getFriendTyping(), state)
            CoreEvents.Event.EventTypeCase.FRIEND_READ_RECEIPT ->
                dispatchFriendReadReceipt(handler, event.getFriendReadReceipt(), state)
            CoreEvents.Event.EventTypeCase.FRIEND_REQUEST ->
                dispatchFriendRequest(handler, event.getFriendRequest(), state)
            CoreEvents.Event.EventTypeCase.FRIEND_MESSAGE ->
                dispatchFriendMessage(handler, event.getFriendMessage(), state)
            CoreEvents.Event.EventTypeCase.FILE_RECV_CONTROL ->
                dispatchFileRecvControl(handler, event.getFileRecvControl(), state)
            CoreEvents.Event.EventTypeCase.FILE_CHUNK_REQUEST ->
                dispatchFileChunkRequest(handler, event.getFileChunkRequest(), state)
            CoreEvents.Event.EventTypeCase.FILE_RECV ->
                dispatchFileRecv(handler, event.getFileRecv(), state)
            CoreEvents.Event.EventTypeCase.FILE_RECV_CHUNK ->
                dispatchFileRecvChunk(handler, event.getFileRecvChunk(), state)
            CoreEvents.Event.EventTypeCase.FRIEND_LOSSY_PACKET ->
                dispatchFriendLossyPacket(handler, event.getFriendLossyPacket(), state)
            CoreEvents.Event.EventTypeCase.FRIEND_LOSSLESS_PACKET ->
                dispatchFriendLosslessPacket(handler, event.getFriendLosslessPacket(), state)
            CoreEvents.Event.EventTypeCase.CONFERENCE_INVITE ->
                dispatchConferenceInvite(handler, event.getConferenceInvite(), state)
            CoreEvents.Event.EventTypeCase.CONFERENCE_CONNECTED ->
                dispatchConferenceConnected(handler, event.getConferenceConnected(), state)
            CoreEvents.Event.EventTypeCase.CONFERENCE_MESSAGE ->
                dispatchConferenceMessage(handler, event.getConferenceMessage(), state)
            CoreEvents.Event.EventTypeCase.CONFERENCE_TITLE ->
                dispatchConferenceTitle(handler, event.getConferenceTitle(), state)
            CoreEvents.Event.EventTypeCase.CONFERENCE_PEER_NAME ->
                dispatchConferencePeerName(handler, event.getConferencePeerName(), state)
            CoreEvents.Event.EventTypeCase.CONFERENCE_PEER_LIST_CHANGED ->
                dispatchConferencePeerListChanged(handler, event.getConferencePeerListChanged(), state)
            CoreEvents.Event.EventTypeCase.GROUP_PEER_NAME ->
                dispatchGroupPeerName(handler, event.getGroupPeerName(), state)
            CoreEvents.Event.EventTypeCase.GROUP_PEER_STATUS ->
                dispatchGroupPeerStatus(handler, event.getGroupPeerStatus(), state)
            CoreEvents.Event.EventTypeCase.GROUP_TOPIC ->
                dispatchGroupTopic(handler, event.getGroupTopic(), state)
            CoreEvents.Event.EventTypeCase.GROUP_PRIVACY_STATE ->
                dispatchGroupPrivacyState(handler, event.getGroupPrivacyState(), state)
            CoreEvents.Event.EventTypeCase.GROUP_VOICE_STATE ->
                dispatchGroupVoiceState(handler, event.getGroupVoiceState(), state)
            CoreEvents.Event.EventTypeCase.GROUP_TOPIC_LOCK ->
                dispatchGroupTopicLock(handler, event.getGroupTopicLock(), state)
            CoreEvents.Event.EventTypeCase.GROUP_PEER_LIMIT ->
                dispatchGroupPeerLimit(handler, event.getGroupPeerLimit(), state)
            CoreEvents.Event.EventTypeCase.GROUP_PASSWORD ->
                dispatchGroupPassword(handler, event.getGroupPassword(), state)
            CoreEvents.Event.EventTypeCase.GROUP_MESSAGE ->
                dispatchGroupMessage(handler, event.getGroupMessage(), state)
            CoreEvents.Event.EventTypeCase.GROUP_PRIVATE_MESSAGE ->
                dispatchGroupPrivateMessage(handler, event.getGroupPrivateMessage(), state)
            CoreEvents.Event.EventTypeCase.GROUP_CUSTOM_PACKET ->
                dispatchGroupCustomPacket(handler, event.getGroupCustomPacket(), state)
            CoreEvents.Event.EventTypeCase.GROUP_CUSTOM_PRIVATE_PACKET ->
                dispatchGroupCustomPrivatePacket(handler, event.getGroupCustomPrivatePacket(), state)
            CoreEvents.Event.EventTypeCase.GROUP_INVITE ->
                dispatchGroupInvite(handler, event.getGroupInvite(), state)
            CoreEvents.Event.EventTypeCase.GROUP_PEER_JOIN ->
                dispatchGroupPeerJoin(handler, event.getGroupPeerJoin(), state)
            CoreEvents.Event.EventTypeCase.GROUP_PEER_EXIT ->
                dispatchGroupPeerExit(handler, event.getGroupPeerExit(), state)
            CoreEvents.Event.EventTypeCase.GROUP_SELF_JOIN ->
                dispatchGroupSelfJoin(handler, event.getGroupSelfJoin(), state)
            CoreEvents.Event.EventTypeCase.GROUP_JOIN_FAIL ->
                dispatchGroupJoinFail(handler, event.getGroupJoinFail(), state)
            CoreEvents.Event.EventTypeCase.GROUP_MODERATION ->
                dispatchGroupModeration(handler, event.getGroupModeration(), state)
            CoreEvents.Event.EventTypeCase.EVENTTYPE_NOT_SET -> state
            null -> state
        }

    private fun <S> dispatchEvents(
        handler: ToxCoreEventListener<S>,
        events: CoreEvents,
        state: S,
    ): S = events.getEventsList().fold(state) { next, ev -> dispatchEvent(handler, ev, next) }

    fun <S> dispatch(
        handler: ToxCoreEventListener<S>,
        eventData: ByteArray?,
        state: S,
    ): S = eventData?.let { dispatchEvents(handler, CoreEvents.parseFrom(it), state) } ?: state
}
