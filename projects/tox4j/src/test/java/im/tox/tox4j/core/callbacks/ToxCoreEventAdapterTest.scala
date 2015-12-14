package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums._
import im.tox.tox4j.core.proto._
import im.tox.tox4j.testing.GetDisjunction._
import org.scalatest.FunSuite

final class ToxCoreEventAdapterTest extends FunSuite {

  private val listener = new ToxCoreEventAdapter[Unit]

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
    listener.fileRecv(0, 0, ToxFileKind.DATA, 0, ToxFilename.fromString("").get)(())
  }

  test[FileRecvChunk] {
    listener.fileRecvChunk(0, 0, 0, Array.empty)(())
  }

  test[FileChunkRequest] {
    listener.fileChunkRequest(0, 0, 0, 0)(())
  }

  test[FriendConnectionStatus] {
    listener.friendConnectionStatus(0, ToxConnection.NONE)(())
  }

  test[FriendMessage] {
    listener.friendMessage(0, ToxMessageType.NORMAL, 0, ToxFriendMessage.fromString("hello").get)(())
  }

  test[FriendName] {
    listener.friendName(0, ToxNickname.fromString("").get)(())
  }

  test[FriendRequest] {
    listener.friendRequest(
      ToxPublicKey.unsafeFromValue(null),
      0,
      ToxFriendRequestMessage.fromString("").get
    )(())
  }

  test[FriendStatus] {
    listener.friendStatus(0, ToxUserStatus.NONE)(())
  }

  test[FriendStatusMessage] {
    listener.friendStatusMessage(0, ToxStatusMessage.fromString("").get)(())
  }

  test[FriendTyping] {
    listener.friendTyping(0, isTyping = false)(())
  }

  test[FriendLosslessPacket] {
    listener.friendLosslessPacket(0, ToxLosslessPacket.fromByteArray(160, Array.empty).get)(())
  }

  test[FriendLossyPacket] {
    listener.friendLossyPacket(0, ToxLossyPacket.fromByteArray(200, Array.empty).get)(())
  }

  test[FriendReadReceipt] {
    listener.friendReadReceipt(0, 0)(())
  }

}
