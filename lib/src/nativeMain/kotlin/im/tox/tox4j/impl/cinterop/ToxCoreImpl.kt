package im.tox.tox4j.impl.cinterop

import im.tox.tox4j.core.ToxCore
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
import im.tox.tox4j.core.options.ToxOptions

/**
 * Initialises the new Tox instance with an optional save-data received from [[getSavedata]].
 *
 * @param options Connection options object with optional save-data.
 */
@kotlin.ExperimentalStdlibApi
final class ToxCoreImpl(val options: ToxOptions) : ToxCore {
    override fun load(options: ToxOptions): ToxCoreImpl = TODO("load")

    override fun close(): Unit = TODO("close")

    protected fun finalize() {
        TODO("finalize")
    }

    override fun bootstrap(
        address: String,
        port: Port,
        publicKey: ToxPublicKey,
    ) {
        TODO("bootstrap")
    }

    override fun addTcpRelay(
        address: String,
        port: Port,
        publicKey: ToxPublicKey,
    ) {
        TODO("addTcpRelay")
    }

    override val getSavedata: ByteArray
        get() = TODO("getSavedata")

    override val getUdpPort: Port
        get() = TODO("getUdpPort")

    override val getTcpPort: Port
        get() = TODO("getTcpPort")

    override val getDhtId: ToxPublicKey
        get() = TODO("getDhtId")

    override val iterationInterval: Int
        get() = TODO("iterationInterval")

    override fun <S> iterate(
        handler: ToxCoreEventListener<S>,
        state: S,
    ): S = TODO("iterate")

    override val getPublicKey: ToxPublicKey
        get() = TODO("getPublicKey")

    override val getSecretKey: ToxSecretKey
        get() = TODO("getSecretKey")

    override fun setNospam(nospam: Int): Unit = TODO("setNospam")

    override val getNospam: Int
        get() = TODO("getNospam")

    override val getAddress: ToxFriendAddress
        get() = TODO("getAddress")

    override fun setName(name: ToxNickname): Unit = TODO("setName")

    override val getName: ToxNickname
        get() = TODO("getName")

    override fun setStatusMessage(message: ToxStatusMessage): Unit = TODO("setStatusMessage")

    override val getStatusMessage: ToxStatusMessage
        get() = TODO("getStatusMessage")

    override fun setStatus(status: ToxUserStatus): Unit = TODO("setStatus")

    override val getStatus: ToxUserStatus
        get() = TODO("getStatus")

    override fun addFriend(
        address: ToxFriendAddress,
        message: ToxFriendRequestMessage,
    ): ToxFriendNumber = TODO("addFriend")

    override fun addFriendNorequest(publicKey: ToxPublicKey): ToxFriendNumber = TODO("addFriendNorequest")

    override fun deleteFriend(friendNumber: ToxFriendNumber): Unit = TODO("deleteFriend")

    override fun friendByPublicKey(publicKey: ToxPublicKey): ToxFriendNumber = TODO("friendByPublicKey")

    override fun getFriendPublicKey(friendNumber: ToxFriendNumber): ToxPublicKey = TODO("getFriendPublicKey")

    override fun friendExists(friendNumber: ToxFriendNumber): Boolean = TODO("friendExists")

    override val getFriendList: IntArray
        get() = TODO("getFriendList")

    override fun setTyping(
        friendNumber: ToxFriendNumber,
        typing: Boolean,
    ): Unit = TODO("setTyping")

    override fun friendSendMessage(
        friendNumber: ToxFriendNumber,
        messageType: ToxMessageType,
        timeDelta: Int,
        message: ToxFriendMessage,
    ): Int = TODO("friendSendMessage")

    override fun fileControl(
        friendNumber: ToxFriendNumber,
        fileNumber: Int,
        control: ToxFileControl,
    ): Unit = TODO("fileControl")

    override fun fileSeek(
        friendNumber: ToxFriendNumber,
        fileNumber: Int,
        position: Long,
    ): Unit = TODO("fileSeek")

    override fun fileSend(
        friendNumber: ToxFriendNumber,
        kind: Int,
        fileSize: Long,
        fileId: ToxFileId,
        filename: ToxFilename,
    ): Int = TODO("fileSend")

    override fun fileSendChunk(
        friendNumber: ToxFriendNumber,
        fileNumber: Int,
        position: Long,
        data: ByteArray,
    ): Unit = TODO("fileSendChunk")

    override fun getFileFileId(
        friendNumber: ToxFriendNumber,
        fileNumber: Int,
    ): ToxFileId = TODO("getFileFileId")

    override fun friendSendLossyPacket(
        friendNumber: ToxFriendNumber,
        data: ToxLossyPacket,
    ): Unit = TODO("friendSendLossyPacket")

    override fun friendSendLosslessPacket(
        friendNumber: ToxFriendNumber,
        data: ToxLosslessPacket,
    ): Unit = TODO("friendSendLosslessPacket")
}
