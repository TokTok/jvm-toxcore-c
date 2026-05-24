package im.tox.tox4j.impl.jni

import com.google.protobuf.ByteString
import im.tox.tox4j.core.ToxCoreConstants
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
import im.tox.tox4j.core.proto.ConferenceConnected
import im.tox.tox4j.core.proto.ConferenceInvite
import im.tox.tox4j.core.proto.ConferenceMessage
import im.tox.tox4j.core.proto.ConferencePeerListChanged
import im.tox.tox4j.core.proto.ConferencePeerName
import im.tox.tox4j.core.proto.ConferenceTitle
import im.tox.tox4j.core.proto.CoreEvents
import im.tox.tox4j.core.proto.FileChunkRequest
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
import im.tox.tox4j.core.proto.GroupInvite
import im.tox.tox4j.core.proto.GroupJoinFail
import im.tox.tox4j.core.proto.GroupMessage
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
import im.tox.tox4j.core.proto.Log
import im.tox.tox4j.core.proto.SelfConnectionStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import im.tox.tox4j.core.proto.ConferenceType as ProtoConferenceType
import im.tox.tox4j.core.proto.Connection as ProtoConnection
import im.tox.tox4j.core.proto.FileControl as ProtoFileControl
import im.tox.tox4j.core.proto.GroupExitType as ProtoGroupExitType
import im.tox.tox4j.core.proto.GroupJoinFailKind as ProtoGroupJoinFailKind
import im.tox.tox4j.core.proto.GroupModEvent as ProtoGroupModEvent
import im.tox.tox4j.core.proto.GroupPrivacyStateKind as ProtoGroupPrivacyStateKind
import im.tox.tox4j.core.proto.GroupTopicLockKind as ProtoGroupTopicLockKind
import im.tox.tox4j.core.proto.GroupVoiceStateKind as ProtoGroupVoiceStateKind
import im.tox.tox4j.core.proto.LogLevel as ProtoLogLevel
import im.tox.tox4j.core.proto.MessageType as ProtoMessageType
import im.tox.tox4j.core.proto.UserStatus as ProtoUserStatus

/**
 * Every field gets a distinct value, and enums a non-zero one, so a swapped
 * or misread argument fails.
 */
@OptIn(kotlin.ExperimentalStdlibApi::class)
class ToxCoreEventDispatchTest {
    // Recording infrastructure

    private sealed interface Event {
        data class SelfConnectionStatus(
            val connectionStatus: ToxConnection,
        ) : Event

        // ByteArray compares by reference, so records hold List<Byte>.
        data class FriendName(
            val friendNumber: ToxFriendNumber,
            val name: List<Byte>,
        ) : Event

        data class FriendStatusMessage(
            val friendNumber: ToxFriendNumber,
            val message: List<Byte>,
        ) : Event

        data class FriendStatus(
            val friendNumber: ToxFriendNumber,
            val status: ToxUserStatus,
        ) : Event

        data class FriendConnectionStatus(
            val friendNumber: ToxFriendNumber,
            val connectionStatus: ToxConnection,
        ) : Event

        data class FriendTyping(
            val friendNumber: ToxFriendNumber,
            val typing: Boolean,
        ) : Event

        data class FriendReadReceipt(
            val friendNumber: ToxFriendNumber,
            val messageId: ToxFriendMessageId,
        ) : Event

        data class FriendRequest(
            val publicKey: List<Byte>,
            val message: List<Byte>,
        ) : Event

        data class FriendMessage(
            val friendNumber: ToxFriendNumber,
            val type: ToxMessageType,
            val message: List<Byte>,
        ) : Event

        data class FriendLossyPacket(
            val friendNumber: ToxFriendNumber,
            val data: List<Byte>,
        ) : Event

        data class FriendLosslessPacket(
            val friendNumber: ToxFriendNumber,
            val data: List<Byte>,
        ) : Event

        data class FileRecvControl(
            val friendNumber: ToxFriendNumber,
            val fileNumber: ToxFileNumber,
            val control: ToxFileControl,
        ) : Event

        data class FileChunkRequest(
            val friendNumber: ToxFriendNumber,
            val fileNumber: ToxFileNumber,
            val position: Long,
            val length: Long,
        ) : Event

        data class FileRecv(
            val friendNumber: ToxFriendNumber,
            val fileNumber: ToxFileNumber,
            val kind: Int,
            val fileSize: Long,
            val filename: List<Byte>,
        ) : Event

        data class FileRecvChunk(
            val friendNumber: ToxFriendNumber,
            val fileNumber: ToxFileNumber,
            val position: Long,
            val data: List<Byte>,
        ) : Event

        data class ConferenceConnected(
            val conferenceNumber: ToxConferenceNumber,
        ) : Event

        data class ConferenceInvite(
            val friendNumber: ToxFriendNumber,
            val type: ToxConferenceType,
            val cookie: List<Byte>,
        ) : Event

        data class ConferenceMessage(
            val conferenceNumber: ToxConferenceNumber,
            val peerNumber: ToxConferencePeerNumber,
            val type: ToxMessageType,
            val message: List<Byte>,
        ) : Event

        data class ConferencePeerListChanged(
            val conferenceNumber: ToxConferenceNumber,
        ) : Event

        data class ConferencePeerName(
            val conferenceNumber: ToxConferenceNumber,
            val peerNumber: ToxConferencePeerNumber,
            val name: List<Byte>,
        ) : Event

        data class ConferenceTitle(
            val conferenceNumber: ToxConferenceNumber,
            val peerNumber: ToxConferencePeerNumber,
            val title: List<Byte>,
        ) : Event

        data class GroupPeerName(
            val groupNumber: ToxGroupNumber,
            val peerId: ToxGroupPeerNumber,
            val name: List<Byte>,
        ) : Event

        data class GroupPeerStatus(
            val groupNumber: ToxGroupNumber,
            val peerId: ToxGroupPeerNumber,
            val status: ToxUserStatus,
        ) : Event

        data class GroupTopic(
            val groupNumber: ToxGroupNumber,
            val peerId: ToxGroupPeerNumber,
            val topic: List<Byte>,
        ) : Event

        data class GroupPrivacyState(
            val groupNumber: ToxGroupNumber,
            val privacyState: ToxGroupPrivacyState,
        ) : Event

        data class GroupPeerLimit(
            val groupNumber: ToxGroupNumber,
            val peerLimit: Int,
        ) : Event

        data class GroupPassword(
            val groupNumber: ToxGroupNumber,
            val password: List<Byte>,
        ) : Event

        data class GroupMessage(
            val groupNumber: ToxGroupNumber,
            val peerId: ToxGroupPeerNumber,
            val messageType: ToxMessageType,
            val message: List<Byte>,
            val messageId: ToxGroupMessageId,
        ) : Event

        data class GroupPrivateMessage(
            val groupNumber: ToxGroupNumber,
            val peerId: ToxGroupPeerNumber,
            val messageType: ToxMessageType,
            val message: List<Byte>,
            val messageId: ToxGroupMessageId,
        ) : Event

        data class GroupCustomPacket(
            val groupNumber: ToxGroupNumber,
            val peerId: ToxGroupPeerNumber,
            val data: List<Byte>,
        ) : Event

        data class GroupCustomPrivatePacket(
            val groupNumber: ToxGroupNumber,
            val peerId: ToxGroupPeerNumber,
            val data: List<Byte>,
        ) : Event

        data class GroupInvite(
            val friendNumber: ToxFriendNumber,
            val inviteData: List<Byte>,
            val groupName: List<Byte>,
        ) : Event

        data class GroupPeerJoin(
            val groupNumber: ToxGroupNumber,
            val peerId: ToxGroupPeerNumber,
        ) : Event

        data class GroupPeerExit(
            val groupNumber: ToxGroupNumber,
            val peerId: ToxGroupPeerNumber,
            val exitType: ToxGroupExitType,
            val name: List<Byte>,
            val partMessage: List<Byte>,
        ) : Event

        data class GroupSelfJoin(
            val groupNumber: ToxGroupNumber,
        ) : Event

        data class GroupJoinFail(
            val groupNumber: ToxGroupNumber,
            val failType: ToxGroupJoinFail,
        ) : Event

        data class GroupModeration(
            val groupNumber: ToxGroupNumber,
            val sourcePeerId: ToxGroupPeerNumber,
            val targetPeerId: ToxGroupPeerNumber,
            val modType: ToxGroupModEvent,
        ) : Event

        data class GroupVoiceState(
            val groupNumber: ToxGroupNumber,
            val voiceState: ToxGroupVoiceState,
        ) : Event

        data class GroupTopicLock(
            val groupNumber: ToxGroupNumber,
            val topicLock: ToxGroupTopicLock,
        ) : Event

        data class Log(
            val level: ToxLogLevel,
            val file: String,
            val line: Int,
            val func: String,
            val message: String,
        ) : Event
    }

    private class Recorder : ToxCoreEventListener<List<Event>> {
        override fun selfConnectionStatus(
            connectionStatus: ToxConnection,
            state: List<Event>,
        ): List<Event> = state + Event.SelfConnectionStatus(connectionStatus)

        override fun friendName(
            friendNumber: ToxFriendNumber,
            name: ByteArray,
            state: List<Event>,
        ): List<Event> = state + Event.FriendName(friendNumber, name.toList())

        override fun friendStatusMessage(
            friendNumber: ToxFriendNumber,
            message: ByteArray,
            state: List<Event>,
        ): List<Event> = state + Event.FriendStatusMessage(friendNumber, message.toList())

        override fun friendStatus(
            friendNumber: ToxFriendNumber,
            status: ToxUserStatus,
            state: List<Event>,
        ): List<Event> = state + Event.FriendStatus(friendNumber, status)

        override fun friendConnectionStatus(
            friendNumber: ToxFriendNumber,
            connectionStatus: ToxConnection,
            state: List<Event>,
        ): List<Event> = state + Event.FriendConnectionStatus(friendNumber, connectionStatus)

        override fun friendTyping(
            friendNumber: ToxFriendNumber,
            typing: Boolean,
            state: List<Event>,
        ): List<Event> = state + Event.FriendTyping(friendNumber, typing)

        override fun friendReadReceipt(
            friendNumber: ToxFriendNumber,
            messageId: ToxFriendMessageId,
            state: List<Event>,
        ): List<Event> = state + Event.FriendReadReceipt(friendNumber, messageId)

        override fun friendRequest(
            publicKey: ToxPublicKey,
            message: ByteArray,
            state: List<Event>,
        ): List<Event> = state + Event.FriendRequest(publicKey.value.toList(), message.toList())

        override fun friendMessage(
            friendNumber: ToxFriendNumber,
            type: ToxMessageType,
            message: ByteArray,
            state: List<Event>,
        ): List<Event> = state + Event.FriendMessage(friendNumber, type, message.toList())

        override fun friendLossyPacket(
            friendNumber: ToxFriendNumber,
            data: ByteArray,
            state: List<Event>,
        ): List<Event> = state + Event.FriendLossyPacket(friendNumber, data.toList())

        override fun friendLosslessPacket(
            friendNumber: ToxFriendNumber,
            data: ByteArray,
            state: List<Event>,
        ): List<Event> = state + Event.FriendLosslessPacket(friendNumber, data.toList())

        override fun fileRecvControl(
            friendNumber: ToxFriendNumber,
            fileNumber: ToxFileNumber,
            control: ToxFileControl,
            state: List<Event>,
        ): List<Event> = state + Event.FileRecvControl(friendNumber, fileNumber, control)

        override fun fileChunkRequest(
            friendNumber: ToxFriendNumber,
            fileNumber: ToxFileNumber,
            position: Long,
            length: Long,
            state: List<Event>,
        ): List<Event> = state + Event.FileChunkRequest(friendNumber, fileNumber, position, length)

        override fun fileRecv(
            friendNumber: ToxFriendNumber,
            fileNumber: ToxFileNumber,
            kind: Int,
            fileSize: Long,
            filename: ByteArray,
            state: List<Event>,
        ): List<Event> = state + Event.FileRecv(friendNumber, fileNumber, kind, fileSize, filename.toList())

        override fun fileRecvChunk(
            friendNumber: ToxFriendNumber,
            fileNumber: ToxFileNumber,
            position: Long,
            data: ByteArray,
            state: List<Event>,
        ): List<Event> = state + Event.FileRecvChunk(friendNumber, fileNumber, position, data.toList())

        override fun conferenceConnected(
            conferenceNumber: ToxConferenceNumber,
            state: List<Event>,
        ): List<Event> = state + Event.ConferenceConnected(conferenceNumber)

        override fun conferenceInvite(
            friendNumber: ToxFriendNumber,
            type: ToxConferenceType,
            cookie: ByteArray,
            state: List<Event>,
        ): List<Event> = state + Event.ConferenceInvite(friendNumber, type, cookie.toList())

        override fun conferenceMessage(
            conferenceNumber: ToxConferenceNumber,
            peerNumber: ToxConferencePeerNumber,
            type: ToxMessageType,
            message: ByteArray,
            state: List<Event>,
        ): List<Event> = state + Event.ConferenceMessage(conferenceNumber, peerNumber, type, message.toList())

        override fun conferencePeerListChanged(
            conferenceNumber: ToxConferenceNumber,
            state: List<Event>,
        ): List<Event> = state + Event.ConferencePeerListChanged(conferenceNumber)

        override fun conferencePeerName(
            conferenceNumber: ToxConferenceNumber,
            peerNumber: ToxConferencePeerNumber,
            name: ByteArray,
            state: List<Event>,
        ): List<Event> = state + Event.ConferencePeerName(conferenceNumber, peerNumber, name.toList())

        override fun conferenceTitle(
            conferenceNumber: ToxConferenceNumber,
            peerNumber: ToxConferencePeerNumber,
            title: ByteArray,
            state: List<Event>,
        ): List<Event> = state + Event.ConferenceTitle(conferenceNumber, peerNumber, title.toList())

        override fun groupPeerName(
            groupNumber: ToxGroupNumber,
            peerId: ToxGroupPeerNumber,
            name: ByteArray,
            state: List<Event>,
        ): List<Event> = state + Event.GroupPeerName(groupNumber, peerId, name.toList())

        override fun groupPeerStatus(
            groupNumber: ToxGroupNumber,
            peerId: ToxGroupPeerNumber,
            status: ToxUserStatus,
            state: List<Event>,
        ): List<Event> = state + Event.GroupPeerStatus(groupNumber, peerId, status)

        override fun groupTopic(
            groupNumber: ToxGroupNumber,
            peerId: ToxGroupPeerNumber,
            topic: ByteArray,
            state: List<Event>,
        ): List<Event> = state + Event.GroupTopic(groupNumber, peerId, topic.toList())

        override fun groupPrivacyState(
            groupNumber: ToxGroupNumber,
            privacyState: ToxGroupPrivacyState,
            state: List<Event>,
        ): List<Event> = state + Event.GroupPrivacyState(groupNumber, privacyState)

        override fun groupPeerLimit(
            groupNumber: ToxGroupNumber,
            peerLimit: Int,
            state: List<Event>,
        ): List<Event> = state + Event.GroupPeerLimit(groupNumber, peerLimit)

        override fun groupPassword(
            groupNumber: ToxGroupNumber,
            password: ByteArray,
            state: List<Event>,
        ): List<Event> = state + Event.GroupPassword(groupNumber, password.toList())

        override fun groupMessage(
            groupNumber: ToxGroupNumber,
            peerId: ToxGroupPeerNumber,
            messageType: ToxMessageType,
            message: ByteArray,
            messageId: ToxGroupMessageId,
            state: List<Event>,
        ): List<Event> = state + Event.GroupMessage(groupNumber, peerId, messageType, message.toList(), messageId)

        override fun groupPrivateMessage(
            groupNumber: ToxGroupNumber,
            peerId: ToxGroupPeerNumber,
            messageType: ToxMessageType,
            message: ByteArray,
            messageId: ToxGroupMessageId,
            state: List<Event>,
        ): List<Event> = state + Event.GroupPrivateMessage(groupNumber, peerId, messageType, message.toList(), messageId)

        override fun groupCustomPacket(
            groupNumber: ToxGroupNumber,
            peerId: ToxGroupPeerNumber,
            data: ByteArray,
            state: List<Event>,
        ): List<Event> = state + Event.GroupCustomPacket(groupNumber, peerId, data.toList())

        override fun groupCustomPrivatePacket(
            groupNumber: ToxGroupNumber,
            peerId: ToxGroupPeerNumber,
            data: ByteArray,
            state: List<Event>,
        ): List<Event> = state + Event.GroupCustomPrivatePacket(groupNumber, peerId, data.toList())

        override fun groupInvite(
            friendNumber: ToxFriendNumber,
            inviteData: ByteArray,
            groupName: ByteArray,
            state: List<Event>,
        ): List<Event> = state + Event.GroupInvite(friendNumber, inviteData.toList(), groupName.toList())

        override fun groupPeerJoin(
            groupNumber: ToxGroupNumber,
            peerId: ToxGroupPeerNumber,
            state: List<Event>,
        ): List<Event> = state + Event.GroupPeerJoin(groupNumber, peerId)

        override fun groupPeerExit(
            groupNumber: ToxGroupNumber,
            peerId: ToxGroupPeerNumber,
            exitType: ToxGroupExitType,
            name: ByteArray,
            partMessage: ByteArray,
            state: List<Event>,
        ): List<Event> =
            state +
                Event.GroupPeerExit(
                    groupNumber,
                    peerId,
                    exitType,
                    name.toList(),
                    partMessage.toList(),
                )

        override fun groupSelfJoin(
            groupNumber: ToxGroupNumber,
            state: List<Event>,
        ): List<Event> = state + Event.GroupSelfJoin(groupNumber)

        override fun groupJoinFail(
            groupNumber: ToxGroupNumber,
            failType: ToxGroupJoinFail,
            state: List<Event>,
        ): List<Event> = state + Event.GroupJoinFail(groupNumber, failType)

        override fun groupModeration(
            groupNumber: ToxGroupNumber,
            sourcePeerId: ToxGroupPeerNumber,
            targetPeerId: ToxGroupPeerNumber,
            modType: ToxGroupModEvent,
            state: List<Event>,
        ): List<Event> = state + Event.GroupModeration(groupNumber, sourcePeerId, targetPeerId, modType)

        override fun groupVoiceState(
            groupNumber: ToxGroupNumber,
            voiceState: ToxGroupVoiceState,
            state: List<Event>,
        ): List<Event> = state + Event.GroupVoiceState(groupNumber, voiceState)

        override fun groupTopicLock(
            groupNumber: ToxGroupNumber,
            topicLock: ToxGroupTopicLock,
            state: List<Event>,
        ): List<Event> = state + Event.GroupTopicLock(groupNumber, topicLock)

        override fun log(
            level: ToxLogLevel,
            file: String,
            line: Int,
            func: String,
            message: String,
            state: List<Event>,
        ): List<Event> = state + Event.Log(level, file, line, func, message)
    }

    private fun dispatch(event: CoreEvents.Event.Builder): List<Event> {
        val payload =
            CoreEvents
                .newBuilder()
                .addEvents(event)
                .build()
                .toByteArray()
        return ToxCoreEventDispatch.dispatch(Recorder(), payload, emptyList())
    }

    private fun publicKeyBytes(): ByteString = ByteString.copyFrom(ByteArray(ToxCoreConstants.PUBLIC_KEY_SIZE) { (it + 1).toByte() })

    // self_connection_status

    @Test
    fun selfConnectionStatus_wrapsEnum() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setSelfConnectionStatus(
                    SelfConnectionStatus.newBuilder().setConnectionStatus(ProtoConnection.Type.UDP),
                ),
            )
        assertEquals(listOf(Event.SelfConnectionStatus(ToxConnection.UDP)), recorded)
    }

    // friend_*

    @Test
    fun friendName_bindsFriendAndName() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setFriendName(
                    FriendName
                        .newBuilder()
                        .setFriendNumber(7)
                        .setName(ByteString.copyFromUtf8("alice")),
                ),
            )
        assertEquals(
            listOf(Event.FriendName(ToxFriendNumber(7), "alice".encodeToByteArray().toList())),
            recorded,
        )
    }

    @Test
    fun friendStatusMessage_bindsFriendAndMessage() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setFriendStatusMessage(
                    FriendStatusMessage
                        .newBuilder()
                        .setFriendNumber(7)
                        .setMessage(ByteString.copyFromUtf8("brb")),
                ),
            )
        assertEquals(
            listOf(Event.FriendStatusMessage(ToxFriendNumber(7), "brb".encodeToByteArray().toList())),
            recorded,
        )
    }

    @Test
    fun friendStatus_wrapsEnum() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setFriendStatus(
                    FriendStatus
                        .newBuilder()
                        .setFriendNumber(7)
                        .setStatus(ProtoUserStatus.Type.AWAY),
                ),
            )
        assertEquals(
            listOf(Event.FriendStatus(ToxFriendNumber(7), ToxUserStatus.AWAY)),
            recorded,
        )
    }

    @Test
    fun friendConnectionStatus_wrapsEnum() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setFriendConnectionStatus(
                    FriendConnectionStatus
                        .newBuilder()
                        .setFriendNumber(7)
                        .setConnectionStatus(ProtoConnection.Type.TCP),
                ),
            )
        assertEquals(
            listOf(Event.FriendConnectionStatus(ToxFriendNumber(7), ToxConnection.TCP)),
            recorded,
        )
    }

    @Test
    fun friendTyping_bindsFriendAndBoolean() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setFriendTyping(
                    FriendTyping.newBuilder().setFriendNumber(7).setTyping(true),
                ),
            )
        assertEquals(listOf(Event.FriendTyping(ToxFriendNumber(7), true)), recorded)
    }

    @Test
    fun friendReadReceipt_bindsFriendAndMessageId() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setFriendReadReceipt(
                    FriendReadReceipt.newBuilder().setFriendNumber(7).setMessageId(29),
                ),
            )
        assertEquals(
            listOf(Event.FriendReadReceipt(ToxFriendNumber(7), ToxFriendMessageId(29))),
            recorded,
        )
    }

    @Test
    fun friendRequest_bindsPublicKeyAndMessage() {
        val pkBytes = ByteArray(ToxCoreConstants.PUBLIC_KEY_SIZE) { (it + 1).toByte() }
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setFriendRequest(
                    FriendRequest
                        .newBuilder()
                        .setPublicKey(ByteString.copyFrom(pkBytes))
                        .setMessage(ByteString.copyFromUtf8("add me")),
                ),
            )
        assertEquals(
            listOf(
                Event.FriendRequest(
                    pkBytes.toList(),
                    "add me".encodeToByteArray().toList(),
                ),
            ),
            recorded,
        )
    }

    @Test
    fun friendMessage_bindsFriendTypeMessage() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setFriendMessage(
                    FriendMessage
                        .newBuilder()
                        .setFriendNumber(7)
                        .setType(ProtoMessageType.Type.ACTION)
                        .setMessage(ByteString.copyFromUtf8("hi")),
                ),
            )
        assertEquals(
            listOf(
                Event.FriendMessage(
                    ToxFriendNumber(7),
                    ToxMessageType.ACTION,
                    "hi".encodeToByteArray().toList(),
                ),
            ),
            recorded,
        )
    }

    @Test
    fun friendLossyPacket_bindsFriendAndData() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setFriendLossyPacket(
                    FriendLossyPacket
                        .newBuilder()
                        .setFriendNumber(7)
                        .setData(ByteString.copyFrom(byteArrayOf(0xC8.toByte(), 0x01))),
                ),
            )
        assertEquals(
            listOf(
                Event.FriendLossyPacket(
                    ToxFriendNumber(7),
                    listOf<Byte>(0xC8.toByte(), 0x01),
                ),
            ),
            recorded,
        )
    }

    @Test
    fun friendLosslessPacket_bindsFriendAndData() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setFriendLosslessPacket(
                    FriendLosslessPacket
                        .newBuilder()
                        .setFriendNumber(7)
                        .setData(ByteString.copyFrom(byteArrayOf(0xA0.toByte(), 0x05))),
                ),
            )
        assertEquals(
            listOf(
                Event.FriendLosslessPacket(
                    ToxFriendNumber(7),
                    listOf<Byte>(0xA0.toByte(), 0x05),
                ),
            ),
            recorded,
        )
    }

    // file_*

    @Test
    fun fileRecvControl_wrapsEnum() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setFileRecvControl(
                    FileRecvControl
                        .newBuilder()
                        .setFriendNumber(7)
                        .setFileNumber(23)
                        .setControl(ProtoFileControl.Type.PAUSE),
                ),
            )
        assertEquals(
            listOf(
                Event.FileRecvControl(ToxFriendNumber(7), ToxFileNumber(23), ToxFileControl.PAUSE),
            ),
            recorded,
        )
    }

    @Test
    fun fileChunkRequest_distinguishesPositionAndLength() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setFileChunkRequest(
                    FileChunkRequest
                        .newBuilder()
                        .setFriendNumber(7)
                        .setFileNumber(23)
                        .setPosition(31L)
                        .setLength(37L),
                ),
            )
        assertEquals(
            listOf(Event.FileChunkRequest(ToxFriendNumber(7), ToxFileNumber(23), 31L, 37L)),
            recorded,
        )
    }

    @Test
    fun fileRecv_bindsAllFiveArgs() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setFileRecv(
                    FileRecv
                        .newBuilder()
                        .setFriendNumber(7)
                        .setFileNumber(23)
                        .setKind(43)
                        .setFileSize(41L)
                        .setFilename(ByteString.copyFromUtf8("note.txt")),
                ),
            )
        assertEquals(
            listOf(
                Event.FileRecv(
                    ToxFriendNumber(7),
                    ToxFileNumber(23),
                    43,
                    41L,
                    "note.txt".encodeToByteArray().toList(),
                ),
            ),
            recorded,
        )
    }

    @Test
    fun fileRecvChunk_bindsPositionAndData() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setFileRecvChunk(
                    FileRecvChunk
                        .newBuilder()
                        .setFriendNumber(7)
                        .setFileNumber(23)
                        .setPosition(31L)
                        .setData(ByteString.copyFrom(byteArrayOf(0x10, 0x20))),
                ),
            )
        assertEquals(
            listOf(
                Event.FileRecvChunk(
                    ToxFriendNumber(7),
                    ToxFileNumber(23),
                    31L,
                    listOf<Byte>(0x10, 0x20),
                ),
            ),
            recorded,
        )
    }

    // conference_*

    @Test
    fun conferenceConnected_bindsConferenceNumber() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setConferenceConnected(
                    ConferenceConnected.newBuilder().setConferenceNumber(11),
                ),
            )
        assertEquals(listOf(Event.ConferenceConnected(ToxConferenceNumber(11))), recorded)
    }

    @Test
    fun conferenceInvite_bindsFriendTypeCookie() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setConferenceInvite(
                    ConferenceInvite
                        .newBuilder()
                        .setFriendNumber(7)
                        .setType(ProtoConferenceType.Type.AV)
                        .setCookie(ByteString.copyFrom(byteArrayOf(0x42))),
                ),
            )
        assertEquals(
            listOf(
                Event.ConferenceInvite(
                    ToxFriendNumber(7),
                    ToxConferenceType.AV,
                    listOf<Byte>(0x42),
                ),
            ),
            recorded,
        )
    }

    @Test
    fun conferenceMessage_bindsAllFour() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setConferenceMessage(
                    ConferenceMessage
                        .newBuilder()
                        .setConferenceNumber(11)
                        .setPeerNumber(13)
                        .setType(ProtoMessageType.Type.ACTION)
                        .setMessage(ByteString.copyFromUtf8("hi all")),
                ),
            )
        assertEquals(
            listOf(
                Event.ConferenceMessage(
                    ToxConferenceNumber(11),
                    ToxConferencePeerNumber(13),
                    ToxMessageType.ACTION,
                    "hi all".encodeToByteArray().toList(),
                ),
            ),
            recorded,
        )
    }

    @Test
    fun conferencePeerListChanged_bindsConferenceNumber() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setConferencePeerListChanged(
                    ConferencePeerListChanged.newBuilder().setConferenceNumber(11),
                ),
            )
        assertEquals(listOf(Event.ConferencePeerListChanged(ToxConferenceNumber(11))), recorded)
    }

    @Test
    fun conferencePeerName_bindsAllThree() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setConferencePeerName(
                    ConferencePeerName
                        .newBuilder()
                        .setConferenceNumber(11)
                        .setPeerNumber(13)
                        .setName(ByteString.copyFromUtf8("bob")),
                ),
            )
        assertEquals(
            listOf(
                Event.ConferencePeerName(
                    ToxConferenceNumber(11),
                    ToxConferencePeerNumber(13),
                    "bob".encodeToByteArray().toList(),
                ),
            ),
            recorded,
        )
    }

    @Test
    fun conferenceTitle_bindsAllThree() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setConferenceTitle(
                    ConferenceTitle
                        .newBuilder()
                        .setConferenceNumber(11)
                        .setPeerNumber(13)
                        .setTitle(ByteString.copyFromUtf8("standup")),
                ),
            )
        assertEquals(
            listOf(
                Event.ConferenceTitle(
                    ToxConferenceNumber(11),
                    ToxConferencePeerNumber(13),
                    "standup".encodeToByteArray().toList(),
                ),
            ),
            recorded,
        )
    }

    // group_*

    @Test
    fun groupPeerName_bindsGroupPeerName() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setGroupPeerName(
                    GroupPeerName
                        .newBuilder()
                        .setGroupNumber(17)
                        .setPeerId(19)
                        .setName(ByteString.copyFromUtf8("eve")),
                ),
            )
        assertEquals(
            listOf(
                Event.GroupPeerName(
                    ToxGroupNumber(17),
                    ToxGroupPeerNumber(19),
                    "eve".encodeToByteArray().toList(),
                ),
            ),
            recorded,
        )
    }

    @Test
    fun groupPeerStatus_wrapsEnum() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setGroupPeerStatus(
                    GroupPeerStatus
                        .newBuilder()
                        .setGroupNumber(17)
                        .setPeerId(19)
                        .setStatus(ProtoUserStatus.Type.BUSY),
                ),
            )
        assertEquals(
            listOf(
                Event.GroupPeerStatus(ToxGroupNumber(17), ToxGroupPeerNumber(19), ToxUserStatus.BUSY),
            ),
            recorded,
        )
    }

    @Test
    fun groupTopic_bindsAllThree() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setGroupTopic(
                    GroupTopic
                        .newBuilder()
                        .setGroupNumber(17)
                        .setPeerId(19)
                        .setTopic(ByteString.copyFromUtf8("agenda")),
                ),
            )
        assertEquals(
            listOf(
                Event.GroupTopic(
                    ToxGroupNumber(17),
                    ToxGroupPeerNumber(19),
                    "agenda".encodeToByteArray().toList(),
                ),
            ),
            recorded,
        )
    }

    @Test
    fun groupPrivacyState_wrapsEnum() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setGroupPrivacyState(
                    GroupPrivacyState
                        .newBuilder()
                        .setGroupNumber(17)
                        .setPrivacyState(ProtoGroupPrivacyStateKind.Type.PRIVATE),
                ),
            )
        assertEquals(
            listOf(Event.GroupPrivacyState(ToxGroupNumber(17), ToxGroupPrivacyState.PRIVATE)),
            recorded,
        )
    }

    @Test
    fun groupPeerLimit_bindsGroupAndLimit() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setGroupPeerLimit(
                    GroupPeerLimit.newBuilder().setGroupNumber(17).setPeerLimit(47),
                ),
            )
        assertEquals(listOf(Event.GroupPeerLimit(ToxGroupNumber(17), 47)), recorded)
    }

    @Test
    fun groupPassword_wrapsPassword() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setGroupPassword(
                    GroupPassword
                        .newBuilder()
                        .setGroupNumber(17)
                        .setPassword(ByteString.copyFromUtf8("secret")),
                ),
            )
        assertEquals(
            listOf(
                Event.GroupPassword(ToxGroupNumber(17), "secret".encodeToByteArray().toList()),
            ),
            recorded,
        )
    }

    @Test
    fun groupMessage_bindsAllFive() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setGroupMessage(
                    GroupMessage
                        .newBuilder()
                        .setGroupNumber(17)
                        .setPeerId(19)
                        .setMessageType(ProtoMessageType.Type.ACTION)
                        .setMessage(ByteString.copyFromUtf8("rolls dice"))
                        .setMessageId(29),
                ),
            )
        assertEquals(
            listOf(
                Event.GroupMessage(
                    ToxGroupNumber(17),
                    ToxGroupPeerNumber(19),
                    ToxMessageType.ACTION,
                    "rolls dice".encodeToByteArray().toList(),
                    ToxGroupMessageId(29),
                ),
            ),
            recorded,
        )
    }

    @Test
    fun groupPrivateMessage_bindsAllFive() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setGroupPrivateMessage(
                    GroupPrivateMessage
                        .newBuilder()
                        .setGroupNumber(17)
                        .setPeerId(19)
                        .setMessageType(ProtoMessageType.Type.NORMAL)
                        .setMessage(ByteString.copyFromUtf8("psst"))
                        .setMessageId(29),
                ),
            )
        assertEquals(
            listOf(
                Event.GroupPrivateMessage(
                    ToxGroupNumber(17),
                    ToxGroupPeerNumber(19),
                    ToxMessageType.NORMAL,
                    "psst".encodeToByteArray().toList(),
                    ToxGroupMessageId(29),
                ),
            ),
            recorded,
        )
    }

    @Test
    fun groupCustomPacket_bindsData() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setGroupCustomPacket(
                    GroupCustomPacket
                        .newBuilder()
                        .setGroupNumber(17)
                        .setPeerId(19)
                        .setData(ByteString.copyFrom(byteArrayOf(0x77))),
                ),
            )
        assertEquals(
            listOf(
                Event.GroupCustomPacket(
                    ToxGroupNumber(17),
                    ToxGroupPeerNumber(19),
                    listOf<Byte>(0x77),
                ),
            ),
            recorded,
        )
    }

    @Test
    fun groupCustomPrivatePacket_bindsData() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setGroupCustomPrivatePacket(
                    GroupCustomPrivatePacket
                        .newBuilder()
                        .setGroupNumber(17)
                        .setPeerId(19)
                        .setData(ByteString.copyFrom(byteArrayOf(0x88.toByte()))),
                ),
            )
        assertEquals(
            listOf(
                Event.GroupCustomPrivatePacket(
                    ToxGroupNumber(17),
                    ToxGroupPeerNumber(19),
                    listOf<Byte>(0x88.toByte()),
                ),
            ),
            recorded,
        )
    }

    @Test
    fun groupInvite_distinguishesInviteDataFromGroupName() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setGroupInvite(
                    GroupInvite
                        .newBuilder()
                        .setFriendNumber(7)
                        .setInviteData(ByteString.copyFrom(byteArrayOf(0xAB.toByte())))
                        .setGroupName(ByteString.copyFromUtf8("project-x")),
                ),
            )
        assertEquals(
            listOf(
                Event.GroupInvite(
                    ToxFriendNumber(7),
                    listOf<Byte>(0xAB.toByte()),
                    "project-x".encodeToByteArray().toList(),
                ),
            ),
            recorded,
        )
    }

    @Test
    fun groupPeerJoin_bindsGroupAndPeer() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setGroupPeerJoin(
                    GroupPeerJoin.newBuilder().setGroupNumber(17).setPeerId(19),
                ),
            )
        assertEquals(
            listOf(Event.GroupPeerJoin(ToxGroupNumber(17), ToxGroupPeerNumber(19))),
            recorded,
        )
    }

    @Test
    fun groupPeerExit_distinguishesNameFromPartMessage() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setGroupPeerExit(
                    GroupPeerExit
                        .newBuilder()
                        .setGroupNumber(17)
                        .setPeerId(19)
                        .setExitType(ProtoGroupExitType.Type.KICK)
                        .setName(ByteString.copyFromUtf8("eve"))
                        .setPartMessage(ByteString.copyFromUtf8("goodbye")),
                ),
            )
        assertEquals(
            listOf(
                Event.GroupPeerExit(
                    ToxGroupNumber(17),
                    ToxGroupPeerNumber(19),
                    ToxGroupExitType.KICK,
                    "eve".encodeToByteArray().toList(),
                    "goodbye".encodeToByteArray().toList(),
                ),
            ),
            recorded,
        )
    }

    @Test
    fun groupSelfJoin_bindsGroupNumber() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setGroupSelfJoin(
                    GroupSelfJoin.newBuilder().setGroupNumber(17),
                ),
            )
        assertEquals(listOf(Event.GroupSelfJoin(ToxGroupNumber(17))), recorded)
    }

    @Test
    fun groupJoinFail_wrapsEnum() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setGroupJoinFail(
                    GroupJoinFail
                        .newBuilder()
                        .setGroupNumber(17)
                        .setFailType(ProtoGroupJoinFailKind.Type.INVALID_PASSWORD),
                ),
            )
        assertEquals(
            listOf(Event.GroupJoinFail(ToxGroupNumber(17), ToxGroupJoinFail.INVALID_PASSWORD)),
            recorded,
        )
    }

    @Test
    fun groupModeration_distinguishesSourceAndTargetPeer() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setGroupModeration(
                    GroupModeration
                        .newBuilder()
                        .setGroupNumber(17)
                        .setSourcePeerId(19)
                        .setTargetPeerId(23)
                        .setModType(ProtoGroupModEvent.Type.OBSERVER),
                ),
            )
        assertEquals(
            listOf(
                Event.GroupModeration(
                    ToxGroupNumber(17),
                    ToxGroupPeerNumber(19),
                    ToxGroupPeerNumber(23),
                    ToxGroupModEvent.OBSERVER,
                ),
            ),
            recorded,
        )
    }

    @Test
    fun groupVoiceState_wrapsEnum() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setGroupVoiceState(
                    GroupVoiceState
                        .newBuilder()
                        .setGroupNumber(17)
                        .setVoiceState(ProtoGroupVoiceStateKind.Type.MODERATOR),
                ),
            )
        assertEquals(
            listOf(Event.GroupVoiceState(ToxGroupNumber(17), ToxGroupVoiceState.MODERATOR)),
            recorded,
        )
    }

    @Test
    fun groupTopicLock_wrapsEnum() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setGroupTopicLock(
                    GroupTopicLock
                        .newBuilder()
                        .setGroupNumber(17)
                        .setTopicLock(ProtoGroupTopicLockKind.Type.DISABLED),
                ),
            )
        assertEquals(
            listOf(Event.GroupTopicLock(ToxGroupNumber(17), ToxGroupTopicLock.DISABLED)),
            recorded,
        )
    }

    // log

    @Test
    fun log_distinguishesFileFuncMessage() {
        val recorded =
            dispatch(
                CoreEvents.Event.newBuilder().setLog(
                    Log
                        .newBuilder()
                        .setLevel(ProtoLogLevel.Type.WARNING)
                        .setFile("network.c")
                        .setLine(53)
                        .setFunc("send_packet")
                        .setMessage("dropped"),
                ),
            )
        assertEquals(
            listOf(Event.Log(ToxLogLevel.WARNING, "network.c", 53, "send_packet", "dropped")),
            recorded,
        )
    }

    // Aggregate behaviour

    @Test
    fun emptyPayload_yieldsNoEvents() {
        assertEquals(emptyList(), ToxCoreEventDispatch.dispatch(Recorder(), ByteArray(0), emptyList()))
    }

    @Test
    fun nullPayload_yieldsNoEvents() {
        assertEquals(emptyList(), ToxCoreEventDispatch.dispatch(Recorder(), null, emptyList()))
    }

    @Test
    fun emptyEventBuilder_isDispatchedAsNoOp() {
        assertEquals(emptyList(), dispatch(CoreEvents.Event.newBuilder()))
    }

    @Test
    fun multipleEvents_fireInOrder() {
        val payload =
            CoreEvents
                .newBuilder()
                .addEvents(
                    CoreEvents.Event.newBuilder().setFriendName(
                        FriendName
                            .newBuilder()
                            .setFriendNumber(7)
                            .setName(ByteString.copyFromUtf8("alice")),
                    ),
                ).addEvents(
                    CoreEvents.Event.newBuilder().setFriendTyping(
                        FriendTyping.newBuilder().setFriendNumber(7).setTyping(false),
                    ),
                ).build()
                .toByteArray()
        assertEquals(
            listOf(
                Event.FriendName(ToxFriendNumber(7), "alice".encodeToByteArray().toList()),
                Event.FriendTyping(ToxFriendNumber(7), false),
            ),
            ToxCoreEventDispatch.dispatch(Recorder(), payload, emptyList()),
        )
    }
}
