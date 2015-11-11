package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core._
import im.tox.tox4j.core.enums._
import im.tox.tox4j.core.proto.Core._
import org.scalatest.FunSuite

final class ToxEventAdapterTest extends FunSuite {

  private val listener = new ToxEventAdapter[Unit]

  def test[T](f: => Unit)(implicit evidence: Manifest[T]): Unit = {
    test(evidence.runtimeClass.getSimpleName)(f)
  }

  test[SelfConnectionStatus] {
    listener.selfConnectionStatus(ToxConnection.NONE)(())
  }

  test[FileRecvControl] {
    listener.fileRecvControl(0, 0, ToxFileControl.RESUME)(())
  }

  test[FileRecv] {
    listener.fileRecv(0, 0, ToxFileKind.DATA, 0, ToxFilename.unsafeFromByteArray(Array.ofDim[Byte](0)))(())
  }

  test[FileRecvChunk] {
    listener.fileRecvChunk(0, 0, 0, Array.ofDim[Byte](0))(())
  }

  test[FileChunkRequest] {
    listener.fileChunkRequest(0, 0, 0, 0)(())
  }

  test[FriendConnectionStatus] {
    listener.friendConnectionStatus(0, ToxConnection.NONE)(())
  }

  test[FriendMessage] {
    listener.friendMessage(0, ToxMessageType.NORMAL, 0, ToxFriendMessage.unsafeFromByteArray(Array.ofDim[Byte](0)))(())
  }

  test[FriendName] {
    listener.friendName(0, ToxNickname.unsafeFromByteArray(Array.ofDim[Byte](0)))(())
  }

  test[FriendRequest] {
    listener.friendRequest(
      ToxPublicKey.unsafeFromByteArray(null),
      0,
      ToxFriendRequestMessage.unsafeFromByteArray(Array.ofDim[Byte](0))
    )(())
  }

  test[FriendStatus] {
    listener.friendStatus(0, ToxUserStatus.NONE)(())
  }

  test[FriendStatusMessage] {
    listener.friendStatusMessage(0, ToxStatusMessage.unsafeFromByteArray(Array.ofDim[Byte](0)))(())
  }

  test[FriendTyping] {
    listener.friendTyping(0, isTyping = false)(())
  }

  test[FriendLosslessPacket] {
    listener.friendLosslessPacket(0, ToxLosslessPacket.unsafeFromByteArray(Array.ofDim[Byte](0)))(())
  }

  test[FriendLossyPacket] {
    listener.friendLossyPacket(0, ToxLossyPacket.unsafeFromByteArray(Array.ofDim[Byte](0)))(())
  }

  test[FriendReadReceipt] {
    listener.friendReadReceipt(0, 0)(())
  }

}
