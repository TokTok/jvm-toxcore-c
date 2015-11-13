package im.tox.tox4j.core.callbacks

import im.tox.core.random.RandomCore
import im.tox.tox4j.TestConstants
import im.tox.tox4j.core._
import im.tox.tox4j.core.enums.{ToxConnection, ToxFileControl, ToxFileKind, ToxMessageType}
import im.tox.tox4j.testing.autotest.{AliceBobTest, AliceBobTestBase}

/**
 * This test intends to simulate the situation of file pause
 * and resume initiated by both the sending side and the receiving side.
 * - Alice initiated the file transmission and Bob accepted
 * - After sending 1/4 of the file, Alice paused the transmission
 * - Bob saw Alice's paused transmission and sent a message to request resuming
 * - Alice resumed the transmission
 * - Bob paused the transmission after receiving 2/4 of the file
 * - Alice saw Bob paused transmission and sent a message to request resuming
 * - Bob resumed the transmission and received all the data
 */
abstract class FilePauseResumeTestBase extends AliceBobTest {

  final val fileData = RandomCore.randomBytes(TestConstants.Iterations * ToxCoreConstants.MaxCustomPacketSize).toSeq

  final case class State(
    aliceSentFileNumber: Int = -1,
    aliceOffset: Long = 0L,
    aliceShouldPause: Int = -1,
    fileId: ToxFileId = ToxFileId.empty,
    receivedData: Array[Byte] = Array.ofDim(fileData.length),
    bobSentFileNumber: Int = -1,
    bobOffset: Long = 0L,
    bobShouldPause: Int = -1
  )

  final override def initialState: State = State()

  abstract class Alice(name: String, expectedFriendName: String) extends ChatClient(name, expectedFriendName) {

    protected def addFriendMessageTask(friendNumber: Int, bobSentFileNumber: Int, fileId: ToxFileId, tox: ToxCore[ChatState])(state: State): State
    protected def addFileRecvTask(friendNumber: Int, bobSentFileNumber: Int, bobOffset: Long, tox: ToxCore[ChatState])(state: State): State

    override def friendConnectionStatus(friendNumber: Int, connection: ToxConnection)(state: ChatState): ChatState = {
      if (isAlice) {
        if (connection != ToxConnection.NONE) {
          debug(s"is now connected to friend $friendNumber")
          debug(s"initiate file sending to friend $friendNumber")
          assert(friendNumber == AliceBobTestBase.FriendNumber)
          state.addTask { (tox, state) =>
            val aliceSentFileNumber = tox.fileSend(
              friendNumber,
              ToxFileKind.DATA,
              fileData.length,
              ToxFileId.empty,
              ToxFilename.fromByteArray(("file for " + expectedFriendName + ".png").getBytes).get
            )
            state.map(_.copy(
              fileId = tox.getFileFileId(friendNumber, aliceSentFileNumber),
              aliceSentFileNumber = aliceSentFileNumber
            ))
          }
        } else {
          state
        }
      } else {
        if (connection != ToxConnection.NONE) {
          debug(s"is now connected to friend $friendNumber")
          assert(friendNumber == AliceBobTestBase.FriendNumber)
        }
        state
      }
    }

    override def fileRecv(friendNumber: Int, fileNumber: Int, kind: Int, fileSize: Long, filename: ToxFilename)(state: ChatState): ChatState = {
      assert(isBob)
      debug(s"received file send request $fileNumber from friend number $friendNumber current offset ${state.get.bobOffset}")
      assert(friendNumber == AliceBobTestBase.FriendNumber)
      assert(kind == ToxFileKind.DATA)
      assert(new String(filename.value) == s"file for $name.png")

      state.addTask { (tox, state) =>
        assert(state.get.bobSentFileNumber == fileNumber)
        state.map(addFileRecvTask(friendNumber, state.get.bobSentFileNumber, state.get.bobOffset, tox))
      }.map(_.copy(bobSentFileNumber = fileNumber))
    }

    override def fileChunkRequest(friendNumber: Int, fileNumber: Int, position: Long, length: Int)(state: ChatState): ChatState = {
      assert(isAlice)
      debug(s"got request for ${length}B from $friendNumber for file $fileNumber at $position")
      assert(length >= 0)
      if (length == 0) {
        debug("finish transmission")
        state.map(_.copy(aliceSentFileNumber = -1)).finish
      } else {
        val nextState = state.addTask { (tox, state) =>
          debug(s"sending ${length}B to $friendNumber from position $position")
          tox.fileSendChunk(friendNumber, fileNumber, position,
            fileData.slice(position.toInt, Math.min(position.toInt + length, fileData.length)).toArray)
          state
        }.map(state => state.copy(aliceOffset = state.aliceOffset + length))
        if (state.get.aliceOffset >= fileData.length / 4 && state.get.aliceShouldPause == -1) {
          nextState
            .map(_.copy(aliceShouldPause = 0))
            .addTask { (tox, state) =>
              tox.fileControl(friendNumber, fileNumber, ToxFileControl.PAUSE)
              debug("pause file transmission")
              state
            }
        } else {
          nextState
        }
      }
    }

    override def fileRecvControl(friendNumber: Int, fileNumber: Int, control: ToxFileControl)(state: ChatState): ChatState = {
      if (isAlice) {
        debug("receive file control from Bob")
        if (control == ToxFileControl.RESUME) {
          if (state.get.aliceShouldPause != 0) {
            debug("bob accept file transmission request")
            state
          } else {
            debug("see bob resume file transmission")
            state.map(_.copy(aliceShouldPause = 1))
          }
        } else if (control == ToxFileControl.PAUSE) {
          state.addTask { (tox, state) =>
            tox.friendSendMessage(friendNumber, ToxMessageType.NORMAL, 0,
              ToxFriendMessage.fromByteArray("Please resume the file transfer".getBytes).get)
            state.map(_.copy(aliceShouldPause = 0))
          }
        } else {
          state
        }
      } else {
        if (control == ToxFileControl.PAUSE) {
          debug("see alice pause file transmission")
          state.addTask { (tox, state) =>
            debug("request to resume file transmission")
            tox.friendSendMessage(friendNumber, ToxMessageType.NORMAL, 0,
              ToxFriendMessage.fromByteArray("Please resume the file transfer".getBytes).get)
            state
          }
        } else {
          state
        }
      }
    }

    override def fileRecvChunk(friendNumber: Int, fileNumber: Int, position: Long, data: Array[Byte])(state: ChatState): ChatState = {
      assert(isBob)
      debug(s"receive file chunk from position $position of length ${data.length} shouldPause ${state.get.bobShouldPause}")
      if (data.length == 0 && state.get.bobOffset == fileData.length) {
        assert(state.get.receivedData sameElements fileData)
        debug("finish transmission")
        state.finish
      } else {
        System.arraycopy(data, 0, state.get.receivedData, position.toInt, data.length)
        val nextState = state.map(state => state.copy(bobOffset = state.bobOffset + data.length))
        if (nextState.get.bobOffset >= fileData.length * 2 / 4 && nextState.get.bobShouldPause == -1) {
          nextState
            .map(_.copy(bobShouldPause = 0))
            .addTask { (tox, state) =>
              debug("send file control to pause")
              tox.fileControl(friendNumber, state.get.bobSentFileNumber, ToxFileControl.PAUSE)
              state
            }
        } else {
          nextState
        }
      }
    }

    override def friendMessage(friendNumber: Int, newType: ToxMessageType, timeDelta: Int, message: ToxFriendMessage)(state: ChatState): ChatState = {
      debug(s"received a message: ${new String(message.value)}")
      assert(new String(message.value) == "Please resume the file transfer")
      state.addTask { (tox, state) =>
        state.map(addFriendMessageTask(friendNumber, state.get.bobSentFileNumber, state.get.fileId, tox))
      }
    }

  }

}
