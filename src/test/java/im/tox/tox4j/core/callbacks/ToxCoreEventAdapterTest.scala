package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums._
import im.tox.tox4j.core.proto._
import im.tox.tox4j.testing.GetDisjunction._
import org.scalatest.FunSuite

final class ToxCoreEventAdapterTest extends FunSuite {

  private val listener = new ToxCoreEventAdapter[Unit]
  private val friendNumber = ToxFriendNumber.fromInt(0).get

  def test[T](f: => Unit)(implicit evidence: Manifest[T]): Unit = {
    test(evidence.runtimeClass.getSimpleName)(f)
  }

  test[SelfConnectionStatus] {
    listener.selfConnectionStatus(ToxConnection.NONE)(())
  }

  test[FileRecvControl] {
    listener.fileRecvControl(friendNumber, 0, ToxFileControl.RESUME)(())
  }

  test[FileRecv] {
    listener.fileRecv(friendNumber, 0, ToxFileKind.DATA, 0, ToxFilename.fromString("").get)(())
  }

  test[FileRecvChunk] {
    listener.fileRecvChunk(friendNumber, 0, 0, Array.empty)(())
  }

  test[FileChunkRequest] {
    listener.fileChunkRequest(friendNumber, 0, 0, 0)(())
  }

  test[FriendConnectionStatus] {
    listener.friendConnectionStatus(friendNumber, ToxConnection.NONE)(())
  }

  test[FriendMessage] {
    listener.friendMessage(friendNumber, ToxMessageType.NORMAL, 0, ToxFriendMessage.fromString("hello").get)(())
  }

  test[FriendName] {
    listener.friendName(friendNumber, ToxNickname.fromString("").get)(())
  }

  test[FriendRequest] {
    listener.friendRequest(
      ToxPublicKey.unsafeFromValue(null),
      0,
      ToxFriendRequestMessage.fromString("").get
    )(())
  }

  test[FriendStatus] {
    listener.friendStatus(friendNumber, ToxUserStatus.NONE)(())
  }

  test[FriendStatusMessage] {
    listener.friendStatusMessage(friendNumber, ToxStatusMessage.fromString("").get)(())
  }

  test[FriendTyping] {
    listener.friendTyping(friendNumber, isTyping = false)(())
  }

  test[FriendLosslessPacket] {
    listener.friendLosslessPacket(friendNumber, ToxLosslessPacket.fromByteArray(160, Array.empty).get)(())
  }

  test[FriendLossyPacket] {
    listener.friendLossyPacket(friendNumber, ToxLossyPacket.fromByteArray(200, Array.empty).get)(())
  }

  test[FriendReadReceipt] {
    listener.friendReadReceipt(friendNumber, 0)(())
  }

}
