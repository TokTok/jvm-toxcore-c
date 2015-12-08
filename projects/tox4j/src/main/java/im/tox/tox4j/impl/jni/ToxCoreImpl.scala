package im.tox.tox4j.impl.jni

import com.typesafe.scalalogging.Logger
import im.tox.core.network.Port
import im.tox.tox4j.OptimisedIdOps._
import im.tox.tox4j.core._
import im.tox.tox4j.core.callbacks._
import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums.{ToxConnection, ToxFileControl, ToxMessageType, ToxUserStatus}
import im.tox.tox4j.core.exceptions._
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.core.proto._
import im.tox.tox4j.impl.ToxImplBase.tryAndLog
import im.tox.tox4j.impl.jni.ToxCoreImpl.{convert, logger}
import im.tox.tox4j.impl.jni.internal.Event
import org.jetbrains.annotations.{NotNull, Nullable}
import org.slf4j.LoggerFactory

// scalastyle:off null
@SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Null"))
private object ToxCoreImpl {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  @throws[ToxBootstrapException]
  private def checkBootstrapArguments(port: Int, @Nullable publicKey: Array[Byte]): Unit = {
    if (port < 0) {
      throw new ToxBootstrapException(ToxBootstrapException.Code.BAD_PORT, "Port cannot be negative")
    }
    if (port > 65535) {
      throw new ToxBootstrapException(ToxBootstrapException.Code.BAD_PORT, "Port cannot exceed 65535")
    }
    if (publicKey ne null) {
      if (publicKey.length < ToxCoreConstants.PublicKeySize) {
        throw new ToxBootstrapException(ToxBootstrapException.Code.BAD_KEY, "Key too short")
      }
      if (publicKey.length > ToxCoreConstants.PublicKeySize) {
        throw new ToxBootstrapException(ToxBootstrapException.Code.BAD_KEY, "Key too long")
      }
    }
  }

  private def convert(status: Connection.Type): ToxConnection = {
    status match {
      case Connection.Type.NONE => ToxConnection.NONE
      case Connection.Type.TCP  => ToxConnection.TCP
      case Connection.Type.UDP  => ToxConnection.UDP
    }
  }

  private def convert(status: UserStatus.Type): ToxUserStatus = {
    status match {
      case UserStatus.Type.NONE => ToxUserStatus.NONE
      case UserStatus.Type.AWAY => ToxUserStatus.AWAY
      case UserStatus.Type.BUSY => ToxUserStatus.BUSY
    }
  }

  private def convert(control: FileControl.Type): ToxFileControl = {
    control match {
      case FileControl.Type.RESUME => ToxFileControl.RESUME
      case FileControl.Type.PAUSE  => ToxFileControl.PAUSE
      case FileControl.Type.CANCEL => ToxFileControl.CANCEL
    }
  }

  private def convert(messageType: MessageType.Type): ToxMessageType = {
    messageType match {
      case MessageType.Type.NORMAL => ToxMessageType.NORMAL
      case MessageType.Type.ACTION => ToxMessageType.ACTION
    }
  }

  private def throwLengthException(name: String, message: String, expectedSize: Int): Unit = {
    throw new IllegalArgumentException(s"$name too $message, must be $expectedSize bytes")
  }

  private def checkLength(name: String, @Nullable bytes: Array[Byte], expectedSize: Int): Unit = {
    if (bytes ne null) {
      if (bytes.length < expectedSize) {
        throwLengthException(name, "short", expectedSize)
      }
      if (bytes.length > expectedSize) {
        throwLengthException(name, "long", expectedSize)
      }
    }
  }

  @throws[ToxSetInfoException]
  private def checkInfoNotNull(info: Array[Byte]): Unit = {
    if (info eq null) {
      throw new ToxSetInfoException(ToxSetInfoException.Code.NULL)
    }
  }

}

/**
 * Initialises the new Tox instance with an optional save-data received from [[getSavedata]].
 *
 * @param options Connection options object with optional save-data.
 */
// scalastyle:off no.finalize number.of.methods
@throws[ToxNewException]("If an error was detected in the configuration or a runtime error occurred.")
final class ToxCoreImpl[ToxCoreState](@NotNull val options: ToxOptions) extends ToxCore[ToxCoreState] {

  private val onCloseCallbacks = new Event

  private var eventListener: ToxEventListener[ToxCoreState] = new ToxEventAdapter // scalastyle:ignore var.field

  /**
   * This field has package visibility for [[ToxAvImpl]].
   */
  private[impl] val instanceNumber =
    ToxCoreJni.toxNew(
      options.ipv6Enabled,
      options.udpEnabled,
      options.proxy.proxyType.ordinal,
      options.proxy.proxyAddress,
      options.proxy.proxyPort,
      options.startPort,
      options.endPort,
      options.tcpPort,
      options.saveData.kind.ordinal,
      options.saveData.data
    )

  /**
   * Add an onClose callback. This event is invoked just before the instance is closed.
   */
  def addOnCloseCallback(callback: () => Unit): Event.Id =
    onCloseCallbacks += callback

  def removeOnCloseCallback(id: Event.Id): Unit =
    onCloseCallbacks -= id

  override def load(options: ToxOptions): ToxCoreImpl[ToxCoreState] =
    new ToxCoreImpl[ToxCoreState](options)

  override def close(): Unit = {
    onCloseCallbacks()
    ToxCoreJni.toxKill(instanceNumber)
  }

  protected override def finalize(): Unit = {
    try {
      close()
      ToxCoreJni.toxFinalize(instanceNumber)
    } catch {
      case e: Throwable =>
        logger.error("Exception caught in finalizer; this indicates a serious problem in native code", e)
    }
    super.finalize()
  }

  @throws[ToxBootstrapException]
  override def bootstrap(address: String, port: Port, publicKey: ToxPublicKey): Unit = {
    ToxCoreImpl.checkBootstrapArguments(port.value, publicKey.value)
    ToxCoreJni.toxBootstrap(instanceNumber, address, port.value, publicKey.value)
  }

  @throws[ToxBootstrapException]
  override def addTcpRelay(address: String, port: Port, publicKey: ToxPublicKey): Unit = {
    ToxCoreImpl.checkBootstrapArguments(port.value, publicKey.value)
    ToxCoreJni.toxAddTcpRelay(instanceNumber, address, port.value, publicKey.value)
  }

  override def getSavedata: Array[Byte] =
    ToxCoreJni.toxGetSavedata(instanceNumber)

  @throws[ToxGetPortException]
  override def getUdpPort: Port =
    Port.unsafeFromInt(ToxCoreJni.toxSelfGetUdpPort(instanceNumber))

  @throws[ToxGetPortException]
  override def getTcpPort: Port =
    Port.unsafeFromInt(ToxCoreJni.toxSelfGetTcpPort(instanceNumber))

  override def getDhtId: ToxPublicKey =
    ToxPublicKey.unsafeFromValue(ToxCoreJni.toxSelfGetDhtId(instanceNumber))

  override def iterationInterval: Int =
    ToxCoreJni.toxIterationInterval(instanceNumber)

  private def dispatchSelfConnectionStatus(selfConnectionStatus: Seq[SelfConnectionStatus])(state: ToxCoreState): ToxCoreState = {
    selfConnectionStatus.foldLeft(state) {
      case (state, SelfConnectionStatus(status)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.selfConnectionStatus(
          convert(status)
        ))
    }
  }

  private def dispatchFriendName(friendName: Seq[FriendName])(state: ToxCoreState): ToxCoreState = {
    friendName.foldLeft(state) {
      case (state, FriendName(friendNumber, name)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.friendName(
          friendNumber,
          ToxNickname.unsafeFromValue(name.toByteArray)
        ))
    }
  }

  private def dispatchFriendStatusMessage(friendStatusMessage: Seq[FriendStatusMessage])(state: ToxCoreState): ToxCoreState = {
    friendStatusMessage.foldLeft(state) {
      case (state, FriendStatusMessage(friendNumber, message)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.friendStatusMessage(
          friendNumber,
          ToxStatusMessage.unsafeFromValue(message.toByteArray)
        ))
    }
  }

  private def dispatchFriendStatus(friendStatus: Seq[FriendStatus])(state: ToxCoreState): ToxCoreState = {
    friendStatus.foldLeft(state) {
      case (state, FriendStatus(friendNumber, status)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.friendStatus(
          friendNumber,
          convert(status)
        ))
    }
  }

  private def dispatchFriendConnectionStatus(friendConnectionStatus: Seq[FriendConnectionStatus])(state: ToxCoreState): ToxCoreState = {
    friendConnectionStatus.foldLeft(state) {
      case (state, FriendConnectionStatus(friendNumber, status)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.friendConnectionStatus(
          friendNumber,
          convert(status)
        ))
    }
  }

  private def dispatchFriendTyping(friendTyping: Seq[FriendTyping])(state: ToxCoreState): ToxCoreState = {
    friendTyping.foldLeft(state) {
      case (state, FriendTyping(friendNumber, isTyping)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.friendTyping(
          friendNumber,
          isTyping
        ))
    }
  }

  private def dispatchFriendReadReceipt(friendReadReceipt: Seq[FriendReadReceipt])(state: ToxCoreState): ToxCoreState = {
    friendReadReceipt.foldLeft(state) {
      case (state, FriendReadReceipt(friendNumber, messageId)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.friendReadReceipt(
          friendNumber,
          messageId
        ))
    }
  }

  private def dispatchFriendRequest(friendRequest: Seq[FriendRequest])(state: ToxCoreState): ToxCoreState = {
    friendRequest.foldLeft(state) {
      case (state, FriendRequest(publicKey, timeDelta, message)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.friendRequest(
          ToxPublicKey.unsafeFromValue(publicKey.toByteArray),
          timeDelta,
          ToxFriendRequestMessage.unsafeFromValue(message.toByteArray)
        ))
    }
  }

  private def dispatchFriendMessage(friendMessage: Seq[FriendMessage])(state: ToxCoreState): ToxCoreState = {
    friendMessage.foldLeft(state) {
      case (state, FriendMessage(friendNumber, messageType, timeDelta, message)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.friendMessage(
          friendNumber,
          convert(messageType),
          timeDelta,
          ToxFriendMessage.unsafeFromValue(message.toByteArray)
        ))
    }
  }

  private def dispatchFileRecvControl(fileRecvControl: Seq[FileRecvControl])(state: ToxCoreState): ToxCoreState = {
    fileRecvControl.foldLeft(state) {
      case (state, FileRecvControl(friendNumber, fileNumber, control)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.fileRecvControl(
          friendNumber,
          fileNumber,
          convert(control)
        ))
    }
  }

  private def dispatchFileChunkRequest(fileChunkRequest: Seq[FileChunkRequest])(state: ToxCoreState): ToxCoreState = {
    fileChunkRequest.foldLeft(state) {
      case (state, FileChunkRequest(friendNumber, fileNumber, position, length)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.fileChunkRequest(
          friendNumber,
          fileNumber,
          position,
          length
        ))
    }
  }

  private def dispatchFileRecv(fileRecv: Seq[FileRecv])(state: ToxCoreState): ToxCoreState = {
    fileRecv.foldLeft(state) {
      case (state, FileRecv(friendNumber, fileNumber, kind, fileSize, filename)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.fileRecv(
          friendNumber,
          fileNumber,
          kind,
          fileSize,
          ToxFilename.unsafeFromValue(filename.toByteArray)
        ))
    }
  }

  private def dispatchFileRecvChunk(fileRecvChunk: Seq[FileRecvChunk])(state: ToxCoreState): ToxCoreState = {
    fileRecvChunk.foldLeft(state) {
      case (state, FileRecvChunk(friendNumber, fileNumber, position, data)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.fileRecvChunk(
          friendNumber,
          fileNumber,
          position,
          data.toByteArray
        ))
    }
  }

  private def dispatchFriendLossyPacket(friendLossyPacket: Seq[FriendLossyPacket])(state: ToxCoreState): ToxCoreState = {
    friendLossyPacket.foldLeft(state) {
      case (state, FriendLossyPacket(friendNumber, data)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.friendLossyPacket(
          friendNumber,
          ToxLossyPacket.unsafeFromValue(data.toByteArray)
        ))
    }
  }

  private def dispatchFriendLosslessPacket(friendLosslessPacket: Seq[FriendLosslessPacket])(state: ToxCoreState): ToxCoreState = {
    friendLosslessPacket.foldLeft(state) {
      case (state, FriendLosslessPacket(friendNumber, data)) =>
        tryAndLog(options.fatalErrors, state, eventListener)(_.friendLosslessPacket(
          friendNumber,
          ToxLosslessPacket.unsafeFromValue(data.toByteArray)
        ))
    }
  }

  private def dispatchEvents(state: ToxCoreState, events: CoreEvents): ToxCoreState = {
    (state
      |> dispatchSelfConnectionStatus(events.selfConnectionStatus)
      |> dispatchFriendName(events.friendName)
      |> dispatchFriendStatusMessage(events.friendStatusMessage)
      |> dispatchFriendStatus(events.friendStatus)
      |> dispatchFriendConnectionStatus(events.friendConnectionStatus)
      |> dispatchFriendTyping(events.friendTyping)
      |> dispatchFriendReadReceipt(events.friendReadReceipt)
      |> dispatchFriendRequest(events.friendRequest)
      |> dispatchFriendMessage(events.friendMessage)
      |> dispatchFileRecvControl(events.fileRecvControl)
      |> dispatchFileChunkRequest(events.fileChunkRequest)
      |> dispatchFileRecv(events.fileRecv)
      |> dispatchFileRecvChunk(events.fileRecvChunk)
      |> dispatchFriendLossyPacket(events.friendLossyPacket)
      |> dispatchFriendLosslessPacket(events.friendLosslessPacket))
  }

  override def iterate(state: ToxCoreState): ToxCoreState = {
    Option(ToxCoreJni.toxIterate(instanceNumber))
      .map(CoreEvents.parseFrom)
      .toIterable.foldLeft(state)(dispatchEvents)
  }

  override def getPublicKey: ToxPublicKey =
    ToxPublicKey.unsafeFromValue(ToxCoreJni.toxSelfGetPublicKey(instanceNumber))

  override def getSecretKey: ToxSecretKey =
    ToxSecretKey.unsafeFromValue(ToxCoreJni.toxSelfGetSecretKey(instanceNumber))

  override def setNospam(nospam: Int): Unit =
    ToxCoreJni.toxSelfSetNospam(instanceNumber, nospam)

  override def getNospam: Int =
    ToxCoreJni.toxSelfGetNospam(instanceNumber)

  override def getAddress: ToxFriendAddress =
    ToxFriendAddress.unsafeFromValue(ToxCoreJni.toxSelfGetAddress(instanceNumber))

  @throws[ToxSetInfoException]
  override def setName(name: ToxNickname): Unit = {
    ToxCoreImpl.checkInfoNotNull(name.value)
    ToxCoreJni.toxSelfSetName(instanceNumber, name.value)
  }

  override def getName: ToxNickname = {
    ToxNickname.unsafeFromValue(ToxCoreJni.toxSelfGetName(instanceNumber))
  }

  @throws[ToxSetInfoException]
  override def setStatusMessage(message: ToxStatusMessage): Unit = {
    ToxCoreImpl.checkInfoNotNull(message.value)
    ToxCoreJni.toxSelfSetStatusMessage(instanceNumber, message.value)
  }

  override def getStatusMessage: ToxStatusMessage =
    ToxStatusMessage.unsafeFromValue(ToxCoreJni.toxSelfGetStatusMessage(instanceNumber))

  override def setStatus(status: ToxUserStatus): Unit =
    ToxCoreJni.toxSelfSetStatus(instanceNumber, status.ordinal)

  override def getStatus: ToxUserStatus =
    ToxUserStatus.values()(ToxCoreJni.toxSelfGetStatus(instanceNumber))

  @throws[ToxFriendAddException]
  override def addFriend(address: ToxFriendAddress, message: ToxFriendRequestMessage): Int = {
    ToxCoreImpl.checkLength("Friend Address", address.value, ToxCoreConstants.AddressSize)
    ToxCoreJni.toxFriendAdd(instanceNumber, address.value, message.value)
  }

  @throws[ToxFriendAddException]
  override def addFriendNorequest(publicKey: ToxPublicKey): Int = {
    ToxCoreImpl.checkLength("Public Key", publicKey.value, ToxCoreConstants.PublicKeySize)
    ToxCoreJni.toxFriendAddNorequest(instanceNumber, publicKey.value)
  }

  @throws[ToxFriendDeleteException]
  override def deleteFriend(friendNumber: Int): Unit =
    ToxCoreJni.toxFriendDelete(instanceNumber, friendNumber)

  @throws[ToxFriendByPublicKeyException]
  override def friendByPublicKey(publicKey: ToxPublicKey): Int =
    ToxCoreJni.toxFriendByPublicKey(instanceNumber, publicKey.value)

  @throws[ToxFriendGetPublicKeyException]
  override def getFriendPublicKey(friendNumber: Int): ToxPublicKey =
    ToxPublicKey.unsafeFromValue(ToxCoreJni.toxFriendGetPublicKey(instanceNumber, friendNumber))

  override def friendExists(friendNumber: Int): Boolean =
    ToxCoreJni.toxFriendExists(instanceNumber, friendNumber)

  override def getFriendList: Array[Int] =
    ToxCoreJni.toxSelfGetFriendList(instanceNumber)

  @throws[ToxSetTypingException]
  override def setTyping(friendNumber: Int, typing: Boolean): Unit =
    ToxCoreJni.toxSelfSetTyping(instanceNumber, friendNumber, typing)

  @throws[ToxFriendSendMessageException]
  override def friendSendMessage(friendNumber: Int, messageType: ToxMessageType, timeDelta: Int, message: ToxFriendMessage): Int =
    ToxCoreJni.toxFriendSendMessage(instanceNumber, friendNumber, messageType.ordinal, timeDelta, message.value)

  @throws[ToxFileControlException]
  override def fileControl(friendNumber: Int, fileNumber: Int, control: ToxFileControl): Unit =
    ToxCoreJni.toxFileControl(instanceNumber, friendNumber, fileNumber, control.ordinal)

  @throws[ToxFileSeekException]
  override def fileSeek(friendNumber: Int, fileNumber: Int, position: Long): Unit =
    ToxCoreJni.toxFileSeek(instanceNumber, friendNumber, fileNumber, position)

  @throws[ToxFileSendException]
  override def fileSend(friendNumber: Int, kind: Int, fileSize: Long, @NotNull fileId: ToxFileId, filename: ToxFilename): Int =
    ToxCoreJni.toxFileSend(instanceNumber, friendNumber, kind, fileSize, fileId.value, filename.value)

  @throws[ToxFileSendChunkException]
  override def fileSendChunk(friendNumber: Int, fileNumber: Int, position: Long, data: Array[Byte]): Unit =
    ToxCoreJni.toxFileSendChunk(instanceNumber, friendNumber, fileNumber, position, data)

  @throws[ToxFileGetException]
  override def getFileFileId(friendNumber: Int, fileNumber: Int): ToxFileId =
    ToxFileId.unsafeFromValue(ToxCoreJni.toxFileGetFileId(instanceNumber, friendNumber, fileNumber))

  @throws[ToxFriendCustomPacketException]
  override def friendSendLossyPacket(friendNumber: Int, data: ToxLossyPacket): Unit =
    ToxCoreJni.toxFriendSendLossyPacket(instanceNumber, friendNumber, data.value)

  @throws[ToxFriendCustomPacketException]
  override def friendSendLosslessPacket(friendNumber: Int, data: ToxLosslessPacket): Unit =
    ToxCoreJni.toxFriendSendLosslessPacket(instanceNumber, friendNumber, data.value)

  override def callback(handler: ToxEventListener[ToxCoreState]): Unit = {
    this.eventListener = handler
  }

  def invokeFriendName(friendNumber: Int, @NotNull name: ToxNickname): Unit =
    ToxCoreJni.invokeFriendName(instanceNumber, friendNumber, name.value)
  def invokeFriendStatusMessage(friendNumber: Int, @NotNull message: Array[Byte]): Unit =
    ToxCoreJni.invokeFriendStatusMessage(instanceNumber, friendNumber, message)
  def invokeFriendStatus(friendNumber: Int, @NotNull status: ToxUserStatus): Unit =
    ToxCoreJni.invokeFriendStatus(instanceNumber, friendNumber, status.ordinal())
  def invokeFriendConnectionStatus(friendNumber: Int, @NotNull connectionStatus: ToxConnection): Unit =
    ToxCoreJni.invokeFriendConnectionStatus(instanceNumber, friendNumber, connectionStatus.ordinal())
  def invokeFriendTyping(friendNumber: Int, isTyping: Boolean): Unit =
    ToxCoreJni.invokeFriendTyping(instanceNumber, friendNumber, isTyping)
  def invokeFriendReadReceipt(friendNumber: Int, messageId: Int): Unit =
    ToxCoreJni.invokeFriendReadReceipt(instanceNumber, friendNumber, messageId)
  def invokeFriendRequest(@NotNull publicKey: ToxPublicKey, timeDelta: Int, @NotNull message: Array[Byte]): Unit =
    ToxCoreJni.invokeFriendRequest(instanceNumber, publicKey.value, timeDelta, message)
  def invokeFriendMessage(friendNumber: Int, @NotNull messageType: ToxMessageType, timeDelta: Int, @NotNull message: Array[Byte]): Unit =
    ToxCoreJni.invokeFriendMessage(instanceNumber, friendNumber, messageType.ordinal(), timeDelta, message)
  def invokeFileChunkRequest(friendNumber: Int, fileNumber: Int, position: Long, length: Int): Unit =
    ToxCoreJni.invokeFileChunkRequest(instanceNumber, friendNumber, fileNumber, position, length)
  def invokeFileRecv(friendNumber: Int, fileNumber: Int, kind: Int, fileSize: Long, @NotNull filename: Array[Byte]): Unit =
    ToxCoreJni.invokeFileRecv(instanceNumber, friendNumber, fileNumber, kind, fileSize, filename)
  def invokeFileRecvChunk(friendNumber: Int, fileNumber: Int, position: Long, @NotNull data: Array[Byte]): Unit =
    ToxCoreJni.invokeFileRecvChunk(instanceNumber, friendNumber, fileNumber, position, data)
  def invokeFileRecvControl(friendNumber: Int, fileNumber: Int, @NotNull control: ToxFileControl): Unit =
    ToxCoreJni.invokeFileRecvControl(instanceNumber, friendNumber, fileNumber, control.ordinal())
  def invokeFriendLossyPacket(friendNumber: Int, @NotNull data: Array[Byte]): Unit =
    ToxCoreJni.invokeFriendLossyPacket(instanceNumber, friendNumber, data)
  def invokeFriendLosslessPacket(friendNumber: Int, @NotNull data: Array[Byte]): Unit =
    ToxCoreJni.invokeFriendLosslessPacket(instanceNumber, friendNumber, data)
  def invokeSelfConnectionStatus(@NotNull connectionStatus: ToxConnection): Unit =
    ToxCoreJni.invokeSelfConnectionStatus(instanceNumber, connectionStatus.ordinal())

}
