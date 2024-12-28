package im.tox.tox4j.impl.jni

import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.ToxCoreConstants
import im.tox.tox4j.core.callbacks.ToxCoreEventListener
import im.tox.tox4j.core.data.Port
import im.tox.tox4j.core.data.ToxConferenceId
import im.tox.tox4j.core.data.ToxConferenceMessage
import im.tox.tox4j.core.data.ToxConferenceNumber
import im.tox.tox4j.core.data.ToxConferenceOfflinePeerNumber
import im.tox.tox4j.core.data.ToxConferencePeerName
import im.tox.tox4j.core.data.ToxConferencePeerNumber
import im.tox.tox4j.core.data.ToxConferenceTitle
import im.tox.tox4j.core.data.ToxFileId
import im.tox.tox4j.core.data.ToxFilename
import im.tox.tox4j.core.data.ToxFriendAddress
import im.tox.tox4j.core.data.ToxFriendMessage
import im.tox.tox4j.core.data.ToxFriendMessageId
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.data.ToxFriendRequestMessage
import im.tox.tox4j.core.data.ToxGroupChatId
import im.tox.tox4j.core.data.ToxGroupMessage
import im.tox.tox4j.core.data.ToxGroupName
import im.tox.tox4j.core.data.ToxGroupNumber
import im.tox.tox4j.core.data.ToxGroupPartMessage
import im.tox.tox4j.core.data.ToxGroupPassword
import im.tox.tox4j.core.data.ToxGroupPeerNumber
import im.tox.tox4j.core.data.ToxGroupTopic
import im.tox.tox4j.core.data.ToxLosslessPacket
import im.tox.tox4j.core.data.ToxLossyPacket
import im.tox.tox4j.core.data.ToxNickname
import im.tox.tox4j.core.data.ToxPublicKey
import im.tox.tox4j.core.data.ToxSecretKey
import im.tox.tox4j.core.data.ToxStatusMessage
import im.tox.tox4j.core.enums.ToxConferenceType
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.core.enums.ToxFileControl
import im.tox.tox4j.core.enums.ToxGroupPrivacyState
import im.tox.tox4j.core.enums.ToxGroupRole
import im.tox.tox4j.core.enums.ToxGroupTopicLock
import im.tox.tox4j.core.enums.ToxGroupVoiceState
import im.tox.tox4j.core.enums.ToxMessageType
import im.tox.tox4j.core.enums.ToxUserStatus
import im.tox.tox4j.core.exceptions.ToxBootstrapException
import im.tox.tox4j.core.options.ToxOptions

/**
 * Initialises the new Tox instance with an optional save-data received from [[getSavedata]].
 *
 * @param options Connection options object with optional save-data.
 */
@kotlin.ExperimentalStdlibApi
final class ToxCoreImpl(
    val options: ToxOptions,
) : ToxCore {
    /** This field has package visibility for [[ToxAvImpl]]. */
    internal val instanceNumber =
        ToxCoreJni.toxNew(
            options.ipv6Enabled,
            options.udpEnabled,
            options.localDiscoveryEnabled,
            options.proxy.proxyType.ordinal,
            options.proxy.proxyAddress,
            options.proxy.proxyPort.toInt(),
            options.startPort.toInt(),
            options.endPort.toInt(),
            options.tcpPort.toInt(),
            options.saveData.kind.ordinal,
            options.saveData.data,
        )

    override fun load(options: ToxOptions): ToxCoreImpl = ToxCoreImpl(options)

    override fun close(): Unit = ToxCoreJni.toxKill(instanceNumber)

    protected fun finalize() {
        close()
        ToxCoreJni.toxFinalize(instanceNumber)
    }

    override fun bootstrap(
        address: String,
        port: Port,
        publicKey: ToxPublicKey,
    ) {
        ToxCoreImpl.checkBootstrapArguments(publicKey.value)
        ToxCoreJni.toxBootstrap(instanceNumber, address, port.value.toInt(), publicKey.value)
    }

    override fun addTcpRelay(
        address: String,
        port: Port,
        publicKey: ToxPublicKey,
    ) {
        ToxCoreImpl.checkBootstrapArguments(publicKey.value)
        ToxCoreJni.toxAddTcpRelay(instanceNumber, address, port.value.toInt(), publicKey.value)
    }

    override val getSavedata: ByteArray
        get() = ToxCoreJni.toxGetSavedata(instanceNumber)

    override val getUdpPort: Port
        get() = Port(ToxCoreJni.toxSelfGetUdpPort(instanceNumber).toUShort())

    override val getTcpPort: Port
        get() = Port(ToxCoreJni.toxSelfGetTcpPort(instanceNumber).toUShort())

    override val getDhtId: ToxPublicKey
        get() = ToxPublicKey(ToxCoreJni.toxSelfGetDhtId(instanceNumber))

    override val iterationInterval: Int
        get() = ToxCoreJni.toxIterationInterval(instanceNumber)

    override fun <S> iterate(
        handler: ToxCoreEventListener<S>,
        state: S,
    ): S = ToxCoreEventDispatch.dispatch(handler, ToxCoreJni.toxIterate(instanceNumber), state)

    override val getPublicKey: ToxPublicKey
        get() = ToxPublicKey(ToxCoreJni.toxSelfGetPublicKey(instanceNumber))

    override val getSecretKey: ToxSecretKey
        get() = ToxSecretKey(ToxCoreJni.toxSelfGetSecretKey(instanceNumber))

    override fun setNospam(nospam: Int): Unit = ToxCoreJni.toxSelfSetNospam(instanceNumber, nospam)

    override val getNospam: Int
        get() = ToxCoreJni.toxSelfGetNospam(instanceNumber)

    override val getAddress: ToxFriendAddress
        get() = ToxFriendAddress(ToxCoreJni.toxSelfGetAddress(instanceNumber))

    override fun setName(name: ToxNickname): Unit = ToxCoreJni.toxSelfSetName(instanceNumber, name.value)

    override val getName: ToxNickname
        get() = ToxNickname(ToxCoreJni.toxSelfGetName(instanceNumber))

    override fun setStatusMessage(message: ToxStatusMessage) {
        ToxCoreJni.toxSelfSetStatusMessage(instanceNumber, message.value)
    }

    override val getStatusMessage: ToxStatusMessage
        get() = ToxStatusMessage(ToxCoreJni.toxSelfGetStatusMessage(instanceNumber))

    override fun setStatus(status: ToxUserStatus): Unit = ToxCoreJni.toxSelfSetStatus(instanceNumber, status.ordinal)

    override val getStatus: ToxUserStatus
        get() = ToxUserStatus.values()[ToxCoreJni.toxSelfGetStatus(instanceNumber)]

    override fun addFriend(
        address: ToxFriendAddress,
        message: ToxFriendRequestMessage,
    ): ToxFriendNumber {
        ToxCoreImpl.checkLength("Friend Address", address.value, ToxCoreConstants.ADDRESS_SIZE)
        return ToxFriendNumber(
            ToxCoreJni.toxFriendAdd(instanceNumber, address.value, message.value),
        )
    }

    override fun addFriendNorequest(publicKey: ToxPublicKey): ToxFriendNumber {
        ToxCoreImpl.checkLength("Public Key", publicKey.value, ToxCoreConstants.PUBLIC_KEY_SIZE)
        return ToxFriendNumber(ToxCoreJni.toxFriendAddNorequest(instanceNumber, publicKey.value))
    }

    override fun deleteFriend(friendNumber: ToxFriendNumber): Unit = ToxCoreJni.toxFriendDelete(instanceNumber, friendNumber.value)

    override fun friendByPublicKey(publicKey: ToxPublicKey): ToxFriendNumber =
        ToxFriendNumber(ToxCoreJni.toxFriendByPublicKey(instanceNumber, publicKey.value))

    override fun getFriendPublicKey(friendNumber: ToxFriendNumber): ToxPublicKey =
        ToxPublicKey(ToxCoreJni.toxFriendGetPublicKey(instanceNumber, friendNumber.value))

    override fun friendExists(friendNumber: ToxFriendNumber): Boolean = ToxCoreJni.toxFriendExists(instanceNumber, friendNumber.value)

    override val getFriendList: IntArray
        get() = ToxCoreJni.toxSelfGetFriendList(instanceNumber)

    override fun setTyping(
        friendNumber: ToxFriendNumber,
        typing: Boolean,
    ): Unit = ToxCoreJni.toxSelfSetTyping(instanceNumber, friendNumber.value, typing)

    override fun friendSendMessage(
        friendNumber: ToxFriendNumber,
        messageType: ToxMessageType,
        message: ToxFriendMessage,
    ): ToxFriendMessageId =
        ToxFriendMessageId(
            ToxCoreJni.toxFriendSendMessage(
                instanceNumber,
                friendNumber.value,
                messageType.ordinal,
                message.value,
            ),
        )

    override fun fileControl(
        friendNumber: ToxFriendNumber,
        fileNumber: Int,
        control: ToxFileControl,
    ): Unit = ToxCoreJni.toxFileControl(instanceNumber, friendNumber.value, fileNumber, control.ordinal)

    override fun fileSeek(
        friendNumber: ToxFriendNumber,
        fileNumber: Int,
        position: Long,
    ): Unit = ToxCoreJni.toxFileSeek(instanceNumber, friendNumber.value, fileNumber, position)

    override fun fileSend(
        friendNumber: ToxFriendNumber,
        kind: Int,
        fileSize: Long,
        fileId: ToxFileId,
        filename: ToxFilename,
    ): Int =
        ToxCoreJni.toxFileSend(
            instanceNumber,
            friendNumber.value,
            kind,
            fileSize,
            fileId.value,
            filename.value,
        )

    override fun fileSendChunk(
        friendNumber: ToxFriendNumber,
        fileNumber: Int,
        position: Long,
        data: ByteArray,
    ): Unit = ToxCoreJni.toxFileSendChunk(instanceNumber, friendNumber.value, fileNumber, position, data)

    override fun getFileFileId(
        friendNumber: ToxFriendNumber,
        fileNumber: Int,
    ): ToxFileId = ToxFileId(ToxCoreJni.toxFileGetFileId(instanceNumber, friendNumber.value, fileNumber))

    override fun friendSendLossyPacket(
        friendNumber: ToxFriendNumber,
        data: ToxLossyPacket,
    ): Unit = ToxCoreJni.toxFriendSendLossyPacket(instanceNumber, friendNumber.value, data.value)

    override fun friendSendLosslessPacket(
        friendNumber: ToxFriendNumber,
        data: ToxLosslessPacket,
    ): Unit = ToxCoreJni.toxFriendSendLosslessPacket(instanceNumber, friendNumber.value, data.value)

    override fun conferenceNew(): ToxConferenceNumber = ToxConferenceNumber(ToxCoreJni.toxConferenceNew(instanceNumber))

    override fun conferenceDelete(conferenceNumber: ToxConferenceNumber): Unit =
        ToxCoreJni.toxConferenceDelete(instanceNumber, conferenceNumber.value)

    override fun conferencePeerCount(conferenceNumber: ToxConferenceNumber): Int =
        ToxCoreJni.toxConferencePeerCount(instanceNumber, conferenceNumber.value)

    override fun conferencePeerGetName(
        conferenceNumber: ToxConferenceNumber,
        peerNumber: ToxConferencePeerNumber,
    ): ToxConferencePeerName =
        ToxConferencePeerName(
            ToxCoreJni.toxConferencePeerGetName(
                instanceNumber,
                conferenceNumber.value,
                peerNumber.value,
            ),
        )

    override fun conferencePeerGetPublicKey(
        conferenceNumber: ToxConferenceNumber,
        peerNumber: ToxConferencePeerNumber,
    ): ToxPublicKey =
        ToxPublicKey(
            ToxCoreJni.toxConferencePeerGetPublicKey(
                instanceNumber,
                conferenceNumber.value,
                peerNumber.value,
            ),
        )

    override fun conferencePeerNumberIsOurs(
        conferenceNumber: ToxConferenceNumber,
        peerNumber: ToxConferencePeerNumber,
    ): Boolean =
        ToxCoreJni.toxConferencePeerNumberIsOurs(
            instanceNumber,
            conferenceNumber.value,
            peerNumber.value,
        )

    override fun conferenceOfflinePeerCount(conferenceNumber: ToxConferenceNumber): Int =
        ToxCoreJni.toxConferenceOfflinePeerCount(instanceNumber, conferenceNumber.value)

    override fun conferenceOfflinePeerGetName(
        conferenceNumber: ToxConferenceNumber,
        offlinePeerNumber: ToxConferenceOfflinePeerNumber,
    ): ToxConferencePeerName =
        ToxConferencePeerName(
            ToxCoreJni.toxConferenceOfflinePeerGetName(
                instanceNumber,
                conferenceNumber.value,
                offlinePeerNumber.value,
            ),
        )

    override fun conferenceOfflinePeerGetPublicKey(
        conferenceNumber: ToxConferenceNumber,
        offlinePeerNumber: ToxConferenceOfflinePeerNumber,
    ): ToxPublicKey =
        ToxPublicKey(
            ToxCoreJni.toxConferenceOfflinePeerGetPublicKey(
                instanceNumber,
                conferenceNumber.value,
                offlinePeerNumber.value,
            ),
        )

    override fun conferenceOfflinePeerGetLastActive(
        conferenceNumber: ToxConferenceNumber,
        offlinePeerNumber: ToxConferenceOfflinePeerNumber,
    ): Long =
        ToxCoreJni.toxConferenceOfflinePeerGetLastActive(
            instanceNumber,
            conferenceNumber.value,
            offlinePeerNumber.value,
        )

    override fun conferenceSetMaxOffline(
        conferenceNumber: ToxConferenceNumber,
        maxOffline: Int,
    ): Unit = ToxCoreJni.toxConferenceSetMaxOffline(instanceNumber, conferenceNumber.value, maxOffline)

    override fun conferenceInvite(
        friendNumber: ToxFriendNumber,
        conferenceNumber: ToxConferenceNumber,
    ): Unit = ToxCoreJni.toxConferenceInvite(instanceNumber, friendNumber.value, conferenceNumber.value)

    override fun conferenceJoin(
        friendNumber: ToxFriendNumber,
        cookie: ByteArray,
    ): ToxConferenceNumber =
        ToxConferenceNumber(
            ToxCoreJni.toxConferenceJoin(instanceNumber, friendNumber.value, cookie),
        )

    override fun conferenceSendMessage(
        conferenceNumber: ToxConferenceNumber,
        messageType: ToxMessageType,
        message: ToxConferenceMessage,
    ): Unit =
        ToxCoreJni.toxConferenceSendMessage(
            instanceNumber,
            conferenceNumber.value,
            messageType.ordinal,
            message.value,
        )

    override fun conferenceGetTitle(conferenceNumber: ToxConferenceNumber): ToxConferenceTitle =
        ToxConferenceTitle(ToxCoreJni.toxConferenceGetTitle(instanceNumber, conferenceNumber.value))

    override fun conferenceSetTitle(
        conferenceNumber: ToxConferenceNumber,
        title: ToxConferenceTitle,
    ): Unit = ToxCoreJni.toxConferenceSetTitle(instanceNumber, conferenceNumber.value, title.value)

    override val conferenceGetChatlist: IntArray
        get() = ToxCoreJni.toxConferenceGetChatlist(instanceNumber)

    override fun conferenceGetType(conferenceNumber: ToxConferenceNumber): ToxConferenceType =
        ToxConferenceType.values()[
            ToxCoreJni.toxConferenceGetType(instanceNumber, conferenceNumber.value),
        ]

    override fun conferenceGetId(conferenceNumber: ToxConferenceNumber): ToxConferenceId =
        ToxConferenceId(ToxCoreJni.toxConferenceGetId(instanceNumber, conferenceNumber.value))

    override fun conferenceById(conferenceId: ToxConferenceId): ToxConferenceNumber =
        ToxConferenceNumber(ToxCoreJni.toxConferenceById(instanceNumber, conferenceId.value))

    override fun groupNew(
        privacyState: ToxGroupPrivacyState,
        groupName: ToxGroupName,
        name: ToxGroupName,
    ): ToxGroupNumber =
        ToxGroupNumber(
            ToxCoreJni.toxGroupNew(
                instanceNumber,
                privacyState.ordinal,
                groupName.value,
                name.value,
            ),
        )

    override fun groupJoin(
        chatId: ToxGroupChatId,
        name: ToxGroupName,
        password: ToxGroupPassword,
    ): ToxGroupNumber =
        ToxGroupNumber(
            ToxCoreJni.toxGroupJoin(instanceNumber, chatId.value, name.value, password.value),
        )

    override fun groupIsConnected(groupNumber: ToxGroupNumber): Boolean = ToxCoreJni.toxGroupIsConnected(instanceNumber, groupNumber.value)

    override fun groupDisconnect(groupNumber: ToxGroupNumber): Unit = ToxCoreJni.toxGroupDisconnect(instanceNumber, groupNumber.value)

    override fun groupLeave(
        groupNumber: ToxGroupNumber,
        partMessage: ToxGroupPartMessage,
    ): Unit = ToxCoreJni.toxGroupLeave(instanceNumber, groupNumber.value, partMessage.value)

    override fun groupSelfSetName(
        groupNumber: ToxGroupNumber,
        name: ToxGroupName,
    ): Unit = ToxCoreJni.toxGroupSelfSetName(instanceNumber, groupNumber.value, name.value)

    override fun groupSelfGetName(groupNumber: ToxGroupNumber): ToxGroupName =
        ToxGroupName(ToxCoreJni.toxGroupSelfGetName(instanceNumber, groupNumber.value))

    override fun groupSelfSetStatus(
        groupNumber: ToxGroupNumber,
        status: ToxUserStatus,
    ): Unit = ToxCoreJni.toxGroupSelfSetStatus(instanceNumber, groupNumber.value, status.ordinal)

    override fun groupSelfGetStatus(groupNumber: ToxGroupNumber): ToxUserStatus =
        ToxUserStatus.values()[ToxCoreJni.toxGroupSelfGetStatus(instanceNumber, groupNumber.value)]

    override fun groupSelfGetRole(groupNumber: ToxGroupNumber): ToxGroupRole =
        ToxGroupRole.values()[ToxCoreJni.toxGroupSelfGetRole(instanceNumber, groupNumber.value)]

    override fun groupSelfGetPeerId(groupNumber: ToxGroupNumber): ToxGroupPeerNumber =
        ToxGroupPeerNumber(ToxCoreJni.toxGroupSelfGetPeerId(instanceNumber, groupNumber.value))

    override fun groupSelfGetPublicKey(groupNumber: ToxGroupNumber): ToxPublicKey =
        ToxPublicKey(ToxCoreJni.toxGroupSelfGetPublicKey(instanceNumber, groupNumber.value))

    override fun groupPeerGetName(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
    ): ToxGroupName =
        ToxGroupName(
            ToxCoreJni.toxGroupPeerGetName(instanceNumber, groupNumber.value, peerId.value),
        )

    override fun groupPeerGetStatus(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
    ): ToxUserStatus =
        ToxUserStatus.values()[
            ToxCoreJni.toxGroupPeerGetStatus(instanceNumber, groupNumber.value, peerId.value),
        ]

    override fun groupPeerGetRole(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
    ): ToxGroupRole =
        ToxGroupRole.values()[
            ToxCoreJni.toxGroupPeerGetRole(instanceNumber, groupNumber.value, peerId.value),
        ]

    override fun groupPeerGetConnectionStatus(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
    ): ToxConnection =
        ToxConnection.values()[
            ToxCoreJni.toxGroupPeerGetConnectionStatus(
                instanceNumber,
                groupNumber.value,
                peerId.value,
            ),
        ]

    override fun groupPeerGetPublicKey(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
    ): ToxPublicKey =
        ToxPublicKey(
            ToxCoreJni.toxGroupPeerGetPublicKey(instanceNumber, groupNumber.value, peerId.value),
        )

    override fun groupSetTopic(
        groupNumber: ToxGroupNumber,
        topic: ToxGroupTopic,
    ): Unit = ToxCoreJni.toxGroupSetTopic(instanceNumber, groupNumber.value, topic.value)

    override fun groupGetTopic(groupNumber: ToxGroupNumber): ToxGroupTopic =
        ToxGroupTopic(ToxCoreJni.toxGroupGetTopic(instanceNumber, groupNumber.value))

    override fun groupGetName(groupNumber: ToxGroupNumber): ToxGroupName =
        ToxGroupName(ToxCoreJni.toxGroupGetName(instanceNumber, groupNumber.value))

    override fun groupGetChatId(groupNumber: ToxGroupNumber): ToxGroupChatId =
        ToxGroupChatId(ToxCoreJni.toxGroupGetChatId(instanceNumber, groupNumber.value))

    override fun groupGetPrivacyState(groupNumber: ToxGroupNumber): ToxGroupPrivacyState =
        ToxGroupPrivacyState.values()[
            ToxCoreJni.toxGroupGetPrivacyState(instanceNumber, groupNumber.value),
        ]

    override fun groupGetVoiceState(groupNumber: ToxGroupNumber): ToxGroupVoiceState =
        ToxGroupVoiceState.values()[
            ToxCoreJni.toxGroupGetVoiceState(instanceNumber, groupNumber.value),
        ]

    override fun groupGetTopicLock(groupNumber: ToxGroupNumber): ToxGroupTopicLock =
        ToxGroupTopicLock.values()[
            ToxCoreJni.toxGroupGetTopicLock(instanceNumber, groupNumber.value),
        ]

    override fun groupGetPeerLimit(groupNumber: ToxGroupNumber): Int = ToxCoreJni.toxGroupGetPeerLimit(instanceNumber, groupNumber.value)

    override fun groupGetPassword(groupNumber: ToxGroupNumber): ToxGroupPassword =
        ToxGroupPassword(ToxCoreJni.toxGroupGetPassword(instanceNumber, groupNumber.value))

    override fun groupSendMessage(
        groupNumber: ToxGroupNumber,
        messageType: ToxMessageType,
        message: ToxGroupMessage,
    ): Int =
        ToxCoreJni.toxGroupSendMessage(
            instanceNumber,
            groupNumber.value,
            messageType.ordinal,
            message.value,
        )

    override fun groupSendPrivateMessage(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
        messageType: ToxMessageType,
        message: ToxGroupMessage,
    ): Int =
        ToxCoreJni.toxGroupSendPrivateMessage(
            instanceNumber,
            groupNumber.value,
            peerId.value,
            messageType.ordinal,
            message.value,
        )

    override fun groupSendCustomPacket(
        groupNumber: ToxGroupNumber,
        lossless: Boolean,
        data: ByteArray,
    ): Unit = ToxCoreJni.toxGroupSendCustomPacket(instanceNumber, groupNumber.value, lossless, data)

    override fun groupSendCustomPrivatePacket(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
        lossless: Boolean,
        data: ByteArray,
    ): Unit =
        ToxCoreJni.toxGroupSendCustomPrivatePacket(
            instanceNumber,
            groupNumber.value,
            peerId.value,
            lossless,
            data,
        )

    override fun groupInviteFriend(
        groupNumber: ToxGroupNumber,
        friendNumber: ToxFriendNumber,
    ): Unit = ToxCoreJni.toxGroupInviteFriend(instanceNumber, groupNumber.value, friendNumber.value)

    override fun groupInviteAccept(
        friendNumber: ToxFriendNumber,
        inviteData: ByteArray,
        name: ToxGroupName,
        password: ToxGroupPassword,
    ): ToxGroupNumber =
        ToxGroupNumber(
            ToxCoreJni.toxGroupInviteAccept(
                instanceNumber,
                friendNumber.value,
                inviteData,
                name.value,
                password.value,
            ),
        )

    override fun groupSetPassword(
        groupNumber: ToxGroupNumber,
        password: ToxGroupPassword,
    ): Unit = ToxCoreJni.toxGroupSetPassword(instanceNumber, groupNumber.value, password.value)

    override fun groupSetTopicLock(
        groupNumber: ToxGroupNumber,
        topicLock: ToxGroupTopicLock,
    ): Unit = ToxCoreJni.toxGroupSetTopicLock(instanceNumber, groupNumber.value, topicLock.ordinal)

    override fun groupSetVoiceState(
        groupNumber: ToxGroupNumber,
        voiceState: ToxGroupVoiceState,
    ): Unit = ToxCoreJni.toxGroupSetVoiceState(instanceNumber, groupNumber.value, voiceState.ordinal)

    override fun groupSetPrivacyState(
        groupNumber: ToxGroupNumber,
        privacyState: ToxGroupPrivacyState,
    ): Unit = ToxCoreJni.toxGroupSetPrivacyState(instanceNumber, groupNumber.value, privacyState.ordinal)

    override fun groupSetPeerLimit(
        groupNumber: ToxGroupNumber,
        peerLimit: Int,
    ): Unit = ToxCoreJni.toxGroupSetPeerLimit(instanceNumber, groupNumber.value, peerLimit)

    override fun groupSetIgnore(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
        ignore: Boolean,
    ): Unit = ToxCoreJni.toxGroupSetIgnore(instanceNumber, groupNumber.value, peerId.value, ignore)

    override fun groupSetRole(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
        role: ToxGroupRole,
    ): Unit = ToxCoreJni.toxGroupSetRole(instanceNumber, groupNumber.value, peerId.value, role.ordinal)

    override fun groupKickPeer(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
    ): Unit = ToxCoreJni.toxGroupKickPeer(instanceNumber, groupNumber.value, peerId.value)

    private companion object {
        fun checkBootstrapArguments(publicKey: ByteArray) {
            if (publicKey.size < ToxCoreConstants.PUBLIC_KEY_SIZE) {
                throw ToxBootstrapException(ToxBootstrapException.Code.BAD_KEY, "Key too short")
            }
            if (publicKey.size > ToxCoreConstants.PUBLIC_KEY_SIZE) {
                throw ToxBootstrapException(ToxBootstrapException.Code.BAD_KEY, "Key too long")
            }
        }

        fun throwLengthException(
            name: String,
            message: String,
            expectedSize: Int,
        ): Unit = throw IllegalArgumentException("$name too $message, must be $expectedSize bytes")

        fun checkLength(
            name: String,
            bytes: ByteArray,
            expectedSize: Int,
        ) {
            if (bytes.size < expectedSize) {
                throwLengthException(name, "short", expectedSize)
            }
            if (bytes.size > expectedSize) {
                throwLengthException(name, "long", expectedSize)
            }
        }
    }
}
