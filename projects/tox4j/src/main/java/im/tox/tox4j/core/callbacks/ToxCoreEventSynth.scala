package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.{ToxPublicKey, ToxNickname}
import im.tox.tox4j.core.enums.{ToxFileControl, ToxMessageType, ToxConnection, ToxUserStatus}
import org.jetbrains.annotations.NotNull

trait ToxCoreEventSynth {

  def invokeFriendName(friendNumber: Int, @NotNull name: ToxNickname): Unit
  def invokeFriendStatusMessage(friendNumber: Int, @NotNull message: Array[Byte]): Unit
  def invokeFriendStatus(friendNumber: Int, @NotNull status: ToxUserStatus): Unit
  def invokeFriendConnectionStatus(friendNumber: Int, @NotNull connectionStatus: ToxConnection): Unit
  def invokeFriendTyping(friendNumber: Int, isTyping: Boolean): Unit
  def invokeFriendReadReceipt(friendNumber: Int, messageId: Int): Unit
  def invokeFriendRequest(@NotNull publicKey: ToxPublicKey, timeDelta: Int, @NotNull message: Array[Byte]): Unit
  def invokeFriendMessage(friendNumber: Int, @NotNull messageType: ToxMessageType, timeDelta: Int, @NotNull message: Array[Byte]): Unit
  def invokeFileChunkRequest(friendNumber: Int, fileNumber: Int, position: Long, length: Int): Unit
  def invokeFileRecv(friendNumber: Int, fileNumber: Int, kind: Int, fileSize: Long, @NotNull filename: Array[Byte]): Unit
  def invokeFileRecvChunk(friendNumber: Int, fileNumber: Int, position: Long, @NotNull data: Array[Byte]): Unit
  def invokeFileRecvControl(friendNumber: Int, fileNumber: Int, @NotNull control: ToxFileControl): Unit
  def invokeFriendLossyPacket(friendNumber: Int, @NotNull data: Array[Byte]): Unit
  def invokeFriendLosslessPacket(friendNumber: Int, @NotNull data: Array[Byte]): Unit
  def invokeSelfConnectionStatus(@NotNull connectionStatus: ToxConnection): Unit

}
