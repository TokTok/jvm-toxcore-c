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
object ToxCoreImpl {

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

  def convert(status: Connection.Type): ToxConnection = {
    status match {
      case Connection.Type.NONE => ToxConnection.NONE
      case Connection.Type.TCP  => ToxConnection.TCP
      case Connection.Type.UDP  => ToxConnection.UDP
    }
  }

  def convert(status: UserStatus.Type): ToxUserStatus = {
    status match {
      case UserStatus.Type.NONE => ToxUserStatus.NONE
      case UserStatus.Type.AWAY => ToxUserStatus.AWAY
      case UserStatus.Type.BUSY => ToxUserStatus.BUSY
    }
  }

  def convert(status: ToxUserStatus): UserStatus.Type = {
    status match {
      case ToxUserStatus.NONE => UserStatus.Type.NONE
      case ToxUserStatus.AWAY => UserStatus.Type.AWAY
      case ToxUserStatus.BUSY => UserStatus.Type.BUSY
    }
  }

  def convert(control: FileControl.Type): ToxFileControl = {
    control match {
      case FileControl.Type.RESUME => ToxFileControl.RESUME
      case FileControl.Type.PAUSE  => ToxFileControl.PAUSE
      case FileControl.Type.CANCEL => ToxFileControl.CANCEL
    }
  }

  def convert(messageType: MessageType.Type): ToxMessageType = {
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
final class ToxCoreImpl(@NotNull val options: ToxOptions) extends ToxCore {

  private val onCloseCallbacks = new Event

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

  override def load(options: ToxOptions): ToxCoreImpl =
    new ToxCoreImpl(options)

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

  private def dispatchSelfConnectionStatus[S](handler: ToxCoreEventListener[S], selfConnectionStatus: Seq[SelfConnectionStatus])(state: S): S = {
    selfConnectionStatus.foldLeft(state) {
      case (state, SelfConnectionStatus(status)) =>
        tryAndLog(options.fatalErrors, state, handler)(_.selfConnectionStatus(
          convert(status)
        ))
    }
  }

  private def dispatchFriendName[S](handler: ToxCoreEventListener[S], friendName: Seq[FriendName])(state: S): S = {
    friendName.foldLeft(state) {
      case (state, FriendName(friendNumber, name)) =>
        tryAndLog(options.fatalErrors, state, handler)(_.friendName(
          ToxFriendNumber.unsafeFromInt(friendNumber),
          ToxNickname.unsafeFromValue(name.toByteArray)
        ))
    }
  }

  private def dispatchFriendStatusMessage[S](handler: ToxCoreEventListener[S], friendStatusMessage: Seq[FriendStatusMessage])(state: S): S = {
    friendStatusMessage.foldLeft(state) {
      case (state, FriendStatusMessage(friendNumber, message)) =>
        tryAndLog(options.fatalErrors, state, handler)(_.friendStatusMessage(
          ToxFriendNumber.unsafeFromInt(friendNumber),
          ToxStatusMessage.unsafeFromValue(message.toByteArray)
        ))
    }
  }

  private def dispatchFriendStatus[S](handler: ToxCoreEventListener[S], friendStatus: Seq[FriendStatus])(state: S): S = {
    friendStatus.foldLeft(state) {
      case (state, FriendStatus(friendNumber, status)) =>
        tryAndLog(options.fatalErrors, state, handler)(_.friendStatus(
          ToxFriendNumber.unsafeFromInt(friendNumber),
          convert(status)
        ))
    }
  }

  private def dispatchFriendConnectionStatus[S](handler: ToxCoreEventListener[S], friendConnectionStatus: Seq[FriendConnectionStatus])(state: S): S = {
    friendConnectionStatus.foldLeft(state) {
      case (state, FriendConnectionStatus(friendNumber, status)) =>
        tryAndLog(options.fatalErrors, state, handler)(_.friendConnectionStatus(
          ToxFriendNumber.unsafeFromInt(friendNumber),
          convert(status)
        ))
    }
  }

  private def dispatchFriendTyping[S](handler: ToxCoreEventListener[S], friendTyping: Seq[FriendTyping])(state: S): S = {
    friendTyping.foldLeft(state) {
      case (state, FriendTyping(friendNumber, isTyping)) =>
        tryAndLog(options.fatalErrors, state, handler)(_.friendTyping(
          ToxFriendNumber.unsafeFromInt(friendNumber),
          isTyping
        ))
    }
  }

  private def dispatchFriendReadReceipt[S](handler: ToxCoreEventListener[S], friendReadReceipt: Seq[FriendReadReceipt])(state: S): S = {
    friendReadReceipt.foldLeft(state) {
      case (state, FriendReadReceipt(friendNumber, messageId)) =>
        tryAndLog(options.fatalErrors, state, handler)(_.friendReadReceipt(
          ToxFriendNumber.unsafeFromInt(friendNumber),
          messageId
        ))
    }
  }

  private def dispatchFriendRequest[S](handler: ToxCoreEventListener[S], friendRequest: Seq[FriendRequest])(state: S): S = {
    friendRequest.foldLeft(state) {
      case (state, FriendRequest(publicKey, timeDelta, message)) =>
        tryAndLog(options.fatalErrors, state, handler)(_.friendRequest(
          ToxPublicKey.unsafeFromValue(publicKey.toByteArray),
          timeDelta,
          ToxFriendRequestMessage.unsafeFromValue(message.toByteArray)
        ))
    }
  }

  private def dispatchFriendMessage[S](handler: ToxCoreEventListener[S], friendMessage: Seq[FriendMessage])(state: S): S = {
    friendMessage.foldLeft(state) {
      case (state, FriendMessage(friendNumber, messageType, timeDelta, message)) =>
        tryAndLog(options.fatalErrors, state, handler)(_.friendMessage(
          ToxFriendNumber.unsafeFromInt(friendNumber),
          convert(messageType),
          timeDelta,
          ToxFriendMessage.unsafeFromValue(message.toByteArray)
        ))
    }
  }

  private def dispatchFileRecvControl[S](handler: ToxCoreEventListener[S], fileRecvControl: Seq[FileRecvControl])(state: S): S = {
    fileRecvControl.foldLeft(state) {
      case (state, FileRecvControl(friendNumber, fileNumber, control)) =>
        tryAndLog(options.fatalErrors, state, handler)(_.fileRecvControl(
          ToxFriendNumber.unsafeFromInt(friendNumber),
          fileNumber,
          convert(control)
        ))
    }
  }

  private def dispatchFileChunkRequest[S](handler: ToxCoreEventListener[S], fileChunkRequest: Seq[FileChunkRequest])(state: S): S = {
    fileChunkRequest.foldLeft(state) {
      case (state, FileChunkRequest(friendNumber, fileNumber, position, length)) =>
        tryAndLog(options.fatalErrors, state, handler)(_.fileChunkRequest(
          ToxFriendNumber.unsafeFromInt(friendNumber),
          fileNumber,
          position,
          length
        ))
    }
  }

  private def dispatchFileRecv[S](handler: ToxCoreEventListener[S], fileRecv: Seq[FileRecv])(state: S): S = {
    fileRecv.foldLeft(state) {
      case (state, FileRecv(friendNumber, fileNumber, kind, fileSize, filename)) =>
        tryAndLog(options.fatalErrors, state, handler)(_.fileRecv(
          ToxFriendNumber.unsafeFromInt(friendNumber),
          fileNumber,
          kind,
          fileSize,
          ToxFilename.unsafeFromValue(filename.toByteArray)
        ))
    }
  }

  private def dispatchFileRecvChunk[S](handler: ToxCoreEventListener[S], fileRecvChunk: Seq[FileRecvChunk])(state: S): S = {
    fileRecvChunk.foldLeft(state) {
      case (state, FileRecvChunk(friendNumber, fileNumber, position, data)) =>
        tryAndLog(options.fatalErrors, state, handler)(_.fileRecvChunk(
          ToxFriendNumber.unsafeFromInt(friendNumber),
          fileNumber,
          position,
          data.toByteArray
        ))
    }
  }

  private def dispatchFriendLossyPacket[S](handler: ToxCoreEventListener[S], friendLossyPacket: Seq[FriendLossyPacket])(state: S): S = {
    friendLossyPacket.foldLeft(state) {
      case (state, FriendLossyPacket(friendNumber, data)) =>
        tryAndLog(options.fatalErrors, state, handler)(_.friendLossyPacket(
          ToxFriendNumber.unsafeFromInt(friendNumber),
          ToxLossyPacket.unsafeFromValue(data.toByteArray)
        ))
    }
  }

  private def dispatchFriendLosslessPacket[S](handler: ToxCoreEventListener[S], friendLosslessPacket: Seq[FriendLosslessPacket])(state: S): S = {
    friendLosslessPacket.foldLeft(state) {
      case (state, FriendLosslessPacket(friendNumber, data)) =>
        tryAndLog(options.fatalErrors, state, handler)(_.friendLosslessPacket(
          ToxFriendNumber.unsafeFromInt(friendNumber),
          ToxLosslessPacket.unsafeFromValue(data.toByteArray)
        ))
    }
  }

  private def dispatchEvents[S](handler: ToxCoreEventListener[S], events: CoreEvents)(state: S): S = {
    (state
      |> dispatchSelfConnectionStatus(handler, events.selfConnectionStatus)
      |> dispatchFriendName(handler, events.friendName)
      |> dispatchFriendStatusMessage(handler, events.friendStatusMessage)
      |> dispatchFriendStatus(handler, events.friendStatus)
      |> dispatchFriendConnectionStatus(handler, events.friendConnectionStatus)
      |> dispatchFriendTyping(handler, events.friendTyping)
      |> dispatchFriendReadReceipt(handler, events.friendReadReceipt)
      |> dispatchFriendRequest(handler, events.friendRequest)
      |> dispatchFriendMessage(handler, events.friendMessage)
      |> dispatchFileRecvControl(handler, events.fileRecvControl)
      |> dispatchFileChunkRequest(handler, events.fileChunkRequest)
      |> dispatchFileRecv(handler, events.fileRecv)
      |> dispatchFileRecvChunk(handler, events.fileRecvChunk)
      |> dispatchFriendLossyPacket(handler, events.friendLossyPacket)
      |> dispatchFriendLosslessPacket(handler, events.friendLosslessPacket))
  }

  @SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Null"))
  override def iterate[S](@NotNull handler: ToxCoreEventListener[S])(state: S): S = {
    val eventData = ToxCoreJni.toxIterate(instanceNumber)
    if (eventData != null) { // scalastyle:ignore null
      val events = CoreEvents.parseFrom(eventData)
      dispatchEvents(handler, events)(state)
    } else {
      state
    }
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
  override def addFriend(address: ToxFriendAddress, message: ToxFriendRequestMessage): ToxFriendNumber = {
    ToxCoreImpl.checkLength("Friend Address", address.value, ToxCoreConstants.AddressSize)
    ToxFriendNumber.unsafeFromInt(ToxCoreJni.toxFriendAdd(instanceNumber, address.value, message.value))
  }

  @throws[ToxFriendAddException]
  override def addFriendNorequest(publicKey: ToxPublicKey): ToxFriendNumber = {
    ToxCoreImpl.checkLength("Public Key", publicKey.value, ToxCoreConstants.PublicKeySize)
    ToxFriendNumber.unsafeFromInt(ToxCoreJni.toxFriendAddNorequest(instanceNumber, publicKey.value))
  }

  @throws[ToxFriendDeleteException]
  override def deleteFriend(friendNumber: ToxFriendNumber): Unit =
    ToxCoreJni.toxFriendDelete(instanceNumber, friendNumber.value)

  @throws[ToxFriendByPublicKeyException]
  override def friendByPublicKey(publicKey: ToxPublicKey): ToxFriendNumber =
    ToxFriendNumber.unsafeFromInt(ToxCoreJni.toxFriendByPublicKey(instanceNumber, publicKey.value))

  @throws[ToxFriendGetPublicKeyException]
  override def getFriendPublicKey(friendNumber: ToxFriendNumber): ToxPublicKey =
    ToxPublicKey.unsafeFromValue(ToxCoreJni.toxFriendGetPublicKey(instanceNumber, friendNumber.value))

  override def friendExists(friendNumber: ToxFriendNumber): Boolean =
    ToxCoreJni.toxFriendExists(instanceNumber, friendNumber.value)

  override def getFriendList: Array[Int] =
    ToxCoreJni.toxSelfGetFriendList(instanceNumber)

  @throws[ToxSetTypingException]
  override def setTyping(friendNumber: ToxFriendNumber, typing: Boolean): Unit =
    ToxCoreJni.toxSelfSetTyping(instanceNumber, friendNumber.value, typing)

  @throws[ToxFriendSendMessageException]
  override def friendSendMessage(friendNumber: ToxFriendNumber, messageType: ToxMessageType, timeDelta: Int, message: ToxFriendMessage): Int =
    ToxCoreJni.toxFriendSendMessage(instanceNumber, friendNumber.value, messageType.ordinal, timeDelta, message.value)

  @throws[ToxFileControlException]
  override def fileControl(friendNumber: ToxFriendNumber, fileNumber: Int, control: ToxFileControl): Unit =
    ToxCoreJni.toxFileControl(instanceNumber, friendNumber.value, fileNumber, control.ordinal)

  @throws[ToxFileSeekException]
  override def fileSeek(friendNumber: ToxFriendNumber, fileNumber: Int, position: Long): Unit =
    ToxCoreJni.toxFileSeek(instanceNumber, friendNumber.value, fileNumber, position)

  @throws[ToxFileSendException]
  override def fileSend(friendNumber: ToxFriendNumber, kind: Int, fileSize: Long, @NotNull fileId: ToxFileId, filename: ToxFilename): Int =
    ToxCoreJni.toxFileSend(instanceNumber, friendNumber.value, kind, fileSize, fileId.value, filename.value)

  @throws[ToxFileSendChunkException]
  override def fileSendChunk(friendNumber: ToxFriendNumber, fileNumber: Int, position: Long, data: Array[Byte]): Unit =
    ToxCoreJni.toxFileSendChunk(instanceNumber, friendNumber.value, fileNumber, position, data)

  @throws[ToxFileGetException]
  override def getFileFileId(friendNumber: ToxFriendNumber, fileNumber: Int): ToxFileId =
    ToxFileId.unsafeFromValue(ToxCoreJni.toxFileGetFileId(instanceNumber, friendNumber.value, fileNumber))

  @throws[ToxFriendCustomPacketException]
  override def friendSendLossyPacket(friendNumber: ToxFriendNumber, data: ToxLossyPacket): Unit =
    ToxCoreJni.toxFriendSendLossyPacket(instanceNumber, friendNumber.value, data.value)

  @throws[ToxFriendCustomPacketException]
  override def friendSendLosslessPacket(friendNumber: ToxFriendNumber, data: ToxLosslessPacket): Unit =
    ToxCoreJni.toxFriendSendLosslessPacket(instanceNumber, friendNumber.value, data.value)

  def invokeFriendName(friendNumber: ToxFriendNumber, @NotNull name: ToxNickname): Unit =
    ToxCoreJni.invokeFriendName(instanceNumber, friendNumber.value, name.value)
  def invokeFriendStatusMessage(friendNumber: ToxFriendNumber, @NotNull message: Array[Byte]): Unit =
    ToxCoreJni.invokeFriendStatusMessage(instanceNumber, friendNumber.value, message)
  def invokeFriendStatus(friendNumber: ToxFriendNumber, @NotNull status: ToxUserStatus): Unit =
    ToxCoreJni.invokeFriendStatus(instanceNumber, friendNumber.value, status.ordinal())
  def invokeFriendConnectionStatus(friendNumber: ToxFriendNumber, @NotNull connectionStatus: ToxConnection): Unit =
    ToxCoreJni.invokeFriendConnectionStatus(instanceNumber, friendNumber.value, connectionStatus.ordinal())
  def invokeFriendTyping(friendNumber: ToxFriendNumber, isTyping: Boolean): Unit =
    ToxCoreJni.invokeFriendTyping(instanceNumber, friendNumber.value, isTyping)
  def invokeFriendReadReceipt(friendNumber: ToxFriendNumber, messageId: Int): Unit =
    ToxCoreJni.invokeFriendReadReceipt(instanceNumber, friendNumber.value, messageId)
  def invokeFriendRequest(@NotNull publicKey: ToxPublicKey, timeDelta: Int, @NotNull message: Array[Byte]): Unit =
    ToxCoreJni.invokeFriendRequest(instanceNumber, publicKey.value, timeDelta, message)
  def invokeFriendMessage(friendNumber: ToxFriendNumber, @NotNull messageType: ToxMessageType, timeDelta: Int, @NotNull message: Array[Byte]): Unit =
    ToxCoreJni.invokeFriendMessage(instanceNumber, friendNumber.value, messageType.ordinal(), timeDelta, message)
  def invokeFileChunkRequest(friendNumber: ToxFriendNumber, fileNumber: Int, position: Long, length: Int): Unit =
    ToxCoreJni.invokeFileChunkRequest(instanceNumber, friendNumber.value, fileNumber, position, length)
  def invokeFileRecv(friendNumber: ToxFriendNumber, fileNumber: Int, kind: Int, fileSize: Long, @NotNull filename: Array[Byte]): Unit =
    ToxCoreJni.invokeFileRecv(instanceNumber, friendNumber.value, fileNumber, kind, fileSize, filename)
  def invokeFileRecvChunk(friendNumber: ToxFriendNumber, fileNumber: Int, position: Long, @NotNull data: Array[Byte]): Unit =
    ToxCoreJni.invokeFileRecvChunk(instanceNumber, friendNumber.value, fileNumber, position, data)
  def invokeFileRecvControl(friendNumber: ToxFriendNumber, fileNumber: Int, @NotNull control: ToxFileControl): Unit =
    ToxCoreJni.invokeFileRecvControl(instanceNumber, friendNumber.value, fileNumber, control.ordinal())
  def invokeFriendLossyPacket(friendNumber: ToxFriendNumber, @NotNull data: Array[Byte]): Unit =
    ToxCoreJni.invokeFriendLossyPacket(instanceNumber, friendNumber.value, data)
  def invokeFriendLosslessPacket(friendNumber: ToxFriendNumber, @NotNull data: Array[Byte]): Unit =
    ToxCoreJni.invokeFriendLosslessPacket(instanceNumber, friendNumber.value, data)
  def invokeSelfConnectionStatus(@NotNull connectionStatus: ToxConnection): Unit =
    ToxCoreJni.invokeSelfConnectionStatus(instanceNumber, connectionStatus.ordinal())

}
