package im.tox.client.callbacks

import im.tox.client.TestState
import im.tox.tox4j.ToxEventListener
import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums.{ToxConnection, ToxFileControl}

final class TestEventListener(id: Int)
    extends IdLogging(id) with ToxEventListener[TestState] {

  override def selfConnectionStatus(connectionStatus: ToxConnection)(state: TestState): TestState = {
    state
  }

  override def friendLossyPacket(friendNumber: ToxFriendNumber, data: ToxLossyPacket)(state: TestState): TestState = {
    state
  }

  override def fileRecv(friendNumber: ToxFriendNumber, fileNumber: Int, kind: Int, fileSize: Long, filename: ToxFilename)(state: TestState): TestState = {
    state
  }

  override def fileChunkRequest(friendNumber: ToxFriendNumber, fileNumber: Int, position: Long, length: Int)(state: TestState): TestState = {
    state
  }

  override def fileRecvChunk(friendNumber: ToxFriendNumber, fileNumber: Int, position: Long, data: Array[Byte])(state: TestState): TestState = {
    state
  }

  override def friendLosslessPacket(friendNumber: ToxFriendNumber, data: ToxLosslessPacket)(state: TestState): TestState = {
    state
  }

  override def fileRecvControl(friendNumber: ToxFriendNumber, fileNumber: Int, control: ToxFileControl)(state: TestState): TestState = {
    state
  }

  override def friendReadReceipt(friendNumber: ToxFriendNumber, messageId: Int)(state: TestState): TestState = {
    state
  }

}
