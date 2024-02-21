package im.tox.tox4j.impl.jni

import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.ToxCoreConstants
import im.tox.tox4j.core.callbacks.ToxCoreEventListener
import im.tox.tox4j.core.data.Port
import im.tox.tox4j.core.data.ToxFileId
import im.tox.tox4j.core.data.ToxFilename
import im.tox.tox4j.core.data.ToxFriendAddress
import im.tox.tox4j.core.data.ToxFriendMessage
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.data.ToxFriendRequestMessage
import im.tox.tox4j.core.data.ToxLosslessPacket
import im.tox.tox4j.core.data.ToxLossyPacket
import im.tox.tox4j.core.data.ToxNickname
import im.tox.tox4j.core.data.ToxPublicKey
import im.tox.tox4j.core.data.ToxSecretKey
import im.tox.tox4j.core.data.ToxStatusMessage
import im.tox.tox4j.core.enums.ToxFileControl
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
final class ToxCoreImpl(val options: ToxOptions) : ToxCore {
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
        return ToxFriendNumber(ToxCoreJni.toxFriendAdd(instanceNumber, address.value, message.value))
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
        timeDelta: Int,
        message: ToxFriendMessage,
    ): Int =
        ToxCoreJni.toxFriendSendMessage(
            instanceNumber,
            friendNumber.value,
            messageType.ordinal,
            timeDelta,
            message.value,
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
        ) {
            throw IllegalArgumentException("$name too $message, must be $expectedSize bytes")
        }

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
