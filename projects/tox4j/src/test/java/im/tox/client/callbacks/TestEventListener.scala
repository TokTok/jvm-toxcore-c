package im.tox.client.callbacks

import im.tox.client.{ToxClientState, ToxClientState$}
import im.tox.tox4j.ToxEventListener
import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums.{ToxConnection, ToxFileControl}

final class TestEventListener(id: Int)
    extends IdLogging(id) with ToxEventListener[ToxClientState] {

  override def selfConnectionStatus(
    connectionStatus: ToxConnection
  )(state: ToxClientState): ToxClientState = {
    state.copy(
      connection = connectionStatus
    )
  }

  override def friendLossyPacket(
    friendNumber: ToxFriendNumber,
    data: ToxLossyPacket
  )(state: ToxClientState): ToxClientState = {
    state
  }

  override def fileRecv(
    friendNumber: ToxFriendNumber,
    fileNumber: Int,
    kind: Int,
    fileSize: Long,
    filename: ToxFilename
  )(state: ToxClientState): ToxClientState = {
    state
  }

  override def fileChunkRequest(
    friendNumber: ToxFriendNumber,
    fileNumber: Int,
    position: Long,
    length: Int
  )(state: ToxClientState): ToxClientState = {
    state
  }

  override def fileRecvChunk(
    friendNumber: ToxFriendNumber,
    fileNumber: Int,
    position: Long,
    data: Array[Byte]
  )(state: ToxClientState): ToxClientState = {
    state
  }

  override def friendLosslessPacket(
    friendNumber: ToxFriendNumber,
    data: ToxLosslessPacket
  )(state: ToxClientState): ToxClientState = {
    state
  }

  override def fileRecvControl(
    friendNumber: ToxFriendNumber,
    fileNumber: Int,
    control: ToxFileControl
  )(state: ToxClientState): ToxClientState = {
    state
  }

  override def friendReadReceipt(
    friendNumber: ToxFriendNumber,
    messageId: Int
  )(state: ToxClientState): ToxClientState = {
    state
  }

}
