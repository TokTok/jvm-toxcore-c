package im.tox.tox4j.impl.jni

import im.tox.core.network.Port
import im.tox.tox4j.core.*
import im.tox.tox4j.core.callbacks.*
import im.tox.tox4j.core.data.*
import im.tox.tox4j.core.enums.*
import im.tox.tox4j.core.exceptions.*
import im.tox.tox4j.core.options.ToxOptions

/**
 * Initialises the new Tox instance with an optional save-data received from [[savedata]].
 *
 * @param options Connection options object with optional save-data.
 */
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

  protected fun finalize(): Unit {
    close()
    ToxCoreJni.toxFinalize(instanceNumber)
  }

  override fun bootstrap(address: String, port: Port, publicKey: ToxPublicKey): Unit {
    ToxCoreImpl.checkBootstrapArguments(publicKey.value)
    ToxCoreJni.toxBootstrap(instanceNumber, address, port.value.toInt(), publicKey.value)
  }

  override fun addTcpRelay(address: String, port: Port, publicKey: ToxPublicKey): Unit {
    ToxCoreImpl.checkBootstrapArguments(publicKey.value)
    ToxCoreJni.toxAddTcpRelay(instanceNumber, address, port.value.toInt(), publicKey.value)
  }

  override val savedata: ByteArray
    get() = ToxCoreJni.toxGetSavedata(instanceNumber)

  override val udpPort: Port
    get() = Port(ToxCoreJni.toxSelfGetUdpPort(instanceNumber).toUShort())

  override val tcpPort: Port
    get() = Port(ToxCoreJni.toxSelfGetTcpPort(instanceNumber).toUShort())

  override val dhtId: ToxPublicKey
    get() = ToxPublicKey(ToxCoreJni.toxSelfGetDhtId(instanceNumber))

  override val iterationInterval: Int
    get() = ToxCoreJni.toxIterationInterval(instanceNumber)

  override fun <S> iterate(handler: ToxCoreEventListener<S>, state: S): S =
      ToxCoreEventDispatch.dispatch(handler, ToxCoreJni.toxIterate(instanceNumber), state)

  override val publicKey: ToxPublicKey
    get() = ToxPublicKey(ToxCoreJni.toxSelfGetPublicKey(instanceNumber))

  override val secretKey: ToxSecretKey
    get() = ToxSecretKey(ToxCoreJni.toxSelfGetSecretKey(instanceNumber))

  override var nospam: Int
    get() = ToxCoreJni.toxSelfGetNospam(instanceNumber)
    set(value) = ToxCoreJni.toxSelfSetNospam(instanceNumber, value)

  override val address: ToxFriendAddress
    get() = ToxFriendAddress(ToxCoreJni.toxSelfGetAddress(instanceNumber))

  override var name: ToxNickname
    get() = ToxNickname(ToxCoreJni.toxSelfGetName(instanceNumber))
    set(value) = ToxCoreJni.toxSelfSetName(instanceNumber, value.value)

  override var statusMessage: ToxStatusMessage
    get() = ToxStatusMessage(ToxCoreJni.toxSelfGetStatusMessage(instanceNumber))
    set(value) = ToxCoreJni.toxSelfSetStatusMessage(instanceNumber, value.value)

  override var status: ToxUserStatus
    get() = ToxUserStatus.values()[ToxCoreJni.toxSelfGetStatus(instanceNumber)]
    set(value) = ToxCoreJni.toxSelfSetStatus(instanceNumber, value.ordinal)

  override fun addFriend(
      address: ToxFriendAddress,
      message: ToxFriendRequestMessage
  ): ToxFriendNumber {
    ToxCoreImpl.checkLength("Friend Address", address.value, ToxCoreConstants.AddressSize)
    return ToxFriendNumber(ToxCoreJni.toxFriendAdd(instanceNumber, address.value, message.value))
  }

  override fun addFriendNorequest(publicKey: ToxPublicKey): ToxFriendNumber {
    ToxCoreImpl.checkLength("Public Key", publicKey.value, ToxCoreConstants.PublicKeySize)
    return ToxFriendNumber(ToxCoreJni.toxFriendAddNorequest(instanceNumber, publicKey.value))
  }

  override fun deleteFriend(friendNumber: ToxFriendNumber): Unit =
      ToxCoreJni.toxFriendDelete(instanceNumber, friendNumber.value)

  override fun friendByPublicKey(publicKey: ToxPublicKey): ToxFriendNumber =
      ToxFriendNumber(ToxCoreJni.toxFriendByPublicKey(instanceNumber, publicKey.value))

  override fun getFriendPublicKey(friendNumber: ToxFriendNumber): ToxPublicKey =
      ToxPublicKey(ToxCoreJni.toxFriendGetPublicKey(instanceNumber, friendNumber.value))

  override fun friendExists(friendNumber: ToxFriendNumber): Boolean =
      ToxCoreJni.toxFriendExists(instanceNumber, friendNumber.value)

  override val friendList: IntArray
    get() = ToxCoreJni.toxSelfGetFriendList(instanceNumber)

  override fun setTyping(friendNumber: ToxFriendNumber, typing: Boolean): Unit =
      ToxCoreJni.toxSelfSetTyping(instanceNumber, friendNumber.value, typing)

  override fun friendSendMessage(
      friendNumber: ToxFriendNumber,
      messageType: ToxMessageType,
      timeDelta: Int,
      message: ToxFriendMessage
  ): Int =
      ToxCoreJni.toxFriendSendMessage(
          instanceNumber, friendNumber.value, messageType.ordinal, timeDelta, message.value)

  override fun fileControl(
      friendNumber: ToxFriendNumber,
      fileNumber: Int,
      control: ToxFileControl
  ): Unit =
      ToxCoreJni.toxFileControl(instanceNumber, friendNumber.value, fileNumber, control.ordinal)

  override fun fileSeek(friendNumber: ToxFriendNumber, fileNumber: Int, position: Long): Unit =
      ToxCoreJni.toxFileSeek(instanceNumber, friendNumber.value, fileNumber, position)

  override fun fileSend(
      friendNumber: ToxFriendNumber,
      kind: Int,
      fileSize: Long,
      fileId: ToxFileId,
      filename: ToxFilename
  ): Int =
      ToxCoreJni.toxFileSend(
          instanceNumber, friendNumber.value, kind, fileSize, fileId.value, filename.value)

  override fun fileSendChunk(
      friendNumber: ToxFriendNumber,
      fileNumber: Int,
      position: Long,
      data: ByteArray
  ): Unit =
      ToxCoreJni.toxFileSendChunk(instanceNumber, friendNumber.value, fileNumber, position, data)

  override fun getFileFileId(friendNumber: ToxFriendNumber, fileNumber: Int): ToxFileId =
      ToxFileId(ToxCoreJni.toxFileGetFileId(instanceNumber, friendNumber.value, fileNumber))

  override fun friendSendLossyPacket(friendNumber: ToxFriendNumber, data: ToxLossyPacket): Unit =
      ToxCoreJni.toxFriendSendLossyPacket(instanceNumber, friendNumber.value, data.value)

  override fun friendSendLosslessPacket(
      friendNumber: ToxFriendNumber,
      data: ToxLosslessPacket
  ): Unit = ToxCoreJni.toxFriendSendLosslessPacket(instanceNumber, friendNumber.value, data.value)

  private companion object {

    fun checkBootstrapArguments(publicKey: ByteArray): Unit {
      if (publicKey.size < ToxCoreConstants.PublicKeySize) {
        throw ToxBootstrapException(ToxBootstrapException.Code.BAD_KEY, "Key too short")
      }
      if (publicKey.size > ToxCoreConstants.PublicKeySize) {
        throw ToxBootstrapException(ToxBootstrapException.Code.BAD_KEY, "Key too long")
      }
    }

    fun throwLengthException(name: String, message: String, expectedSize: Int): Unit {
      throw IllegalArgumentException("${name} too ${message}, must be ${expectedSize} bytes")
    }

    fun checkLength(name: String, bytes: ByteArray, expectedSize: Int): Unit {
      if (bytes.size < expectedSize) {
        throwLengthException(name, "short", expectedSize)
      }
      if (bytes.size > expectedSize) {
        throwLengthException(name, "long", expectedSize)
      }
    }
  }
}