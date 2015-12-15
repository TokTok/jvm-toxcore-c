package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.{ToxFriendNumber, ToxPublicKey, ToxNickname}
import im.tox.tox4j.core.enums.{ToxFileControl, ToxMessageType, ToxConnection, ToxUserStatus}
import org.jetbrains.annotations.NotNull

trait ToxCoreEventSynth {

  def invokeFriendName(friendNumber: ToxFriendNumber, @NotNull name: ToxNickname): Unit
  def invokeFriendStatusMessage(friendNumber: ToxFriendNumber, @NotNull message: Array[Byte]): Unit
  def invokeFriendStatus(friendNumber: ToxFriendNumber, @NotNull status: ToxUserStatus): Unit
  def invokeFriendConnectionStatus(friendNumber: ToxFriendNumber, @NotNull connectionStatus: ToxConnection): Unit
  def invokeFriendTyping(friendNumber: ToxFriendNumber, isTyping: Boolean): Unit
  def invokeFriendReadReceipt(friendNumber: ToxFriendNumber, messageId: Int): Unit
  def invokeFriendRequest(@NotNull publicKey: ToxPublicKey, timeDelta: Int, @NotNull message: Array[Byte]): Unit
  def invokeFriendMessage(friendNumber: ToxFriendNumber, @NotNull messageType: ToxMessageType, timeDelta: Int, @NotNull message: Array[Byte]): Unit
  def invokeFileChunkRequest(friendNumber: ToxFriendNumber, fileNumber: Int, position: Long, length: Int): Unit
  def invokeFileRecv(friendNumber: ToxFriendNumber, fileNumber: Int, kind: Int, fileSize: Long, @NotNull filename: Array[Byte]): Unit
  def invokeFileRecvChunk(friendNumber: ToxFriendNumber, fileNumber: Int, position: Long, @NotNull data: Array[Byte]): Unit
  def invokeFileRecvControl(friendNumber: ToxFriendNumber, fileNumber: Int, @NotNull control: ToxFileControl): Unit
  def invokeFriendLossyPacket(friendNumber: ToxFriendNumber, @NotNull data: Array[Byte]): Unit
  def invokeFriendLosslessPacket(friendNumber: ToxFriendNumber, @NotNull data: Array[Byte]): Unit
  def invokeSelfConnectionStatus(@NotNull connectionStatus: ToxConnection): Unit

}
