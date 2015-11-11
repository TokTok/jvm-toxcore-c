package im.tox.tox4j.core.callbacks

import java.util.Random

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

  final override type State = Unit
  final override def initialState: State = ()

  protected val fileData = new Array[Byte](TestConstants.Iterations * ToxCoreConstants.MaxCustomPacketSize)
  new Random().nextBytes(fileData)
  protected var aliceSentFileNumber = -1
  private var aliceOffset = 0L
  protected var aliceShouldPause = -1
  private var fileId = ToxFileId.empty
  private val receivedData = new Array[Byte](fileData.length)
  private var bobSentFileNumber = -1
  private var bobOffset = 0L
  protected var bobShouldPause = -1

  abstract class Alice(name: String, expectedFriendName: String) extends ChatClient(name, expectedFriendName) {

    protected def addFriendMessageTask(friendNumber: Int, bobSentFileNumber: Int, fileId: ToxFileId, tox: ToxCore[ChatState]): Unit
    protected def addFileRecvTask(friendNumber: Int, fileNumber: Int, bobSentFileNumber: Int, bobOffset: Long, tox: ToxCore[ChatState]): Unit

    override def friendConnectionStatus(friendNumber: Int, connection: ToxConnection)(state: ChatState): ChatState = {
      if (isAlice) {
        if (connection != ToxConnection.NONE) {
          debug(s"is now connected to friend $friendNumber")
          debug(s"initiate file sending to friend $friendNumber")
          assert(friendNumber == AliceBobTestBase.FriendNumber)
          state.addTask { (tox, state) =>
            aliceSentFileNumber = tox.fileSend(
              friendNumber,
              ToxFileKind.DATA,
              fileData.length,
              ToxFileId.empty,
              ToxFilename.unsafeFromByteArray(("file for " + expectedFriendName + ".png").getBytes)
            )
            fileId = tox.getFileFileId(friendNumber, aliceSentFileNumber)
            state
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
      debug(s"received file send request $fileNumber from friend number $friendNumber current offset $bobOffset")
      assert(friendNumber == AliceBobTestBase.FriendNumber)
      assert(kind == ToxFileKind.DATA)
      assert(new String(filename.value) == s"file for $name.png")
      bobSentFileNumber = fileNumber
      state.addTask { (tox, state) =>
        addFileRecvTask(friendNumber, fileNumber, bobSentFileNumber, bobOffset, tox)
        state
      }
    }

    override def fileChunkRequest(friendNumber: Int, fileNumber: Int, position: Long, length: Int)(state: ChatState): ChatState = {
      assert(isAlice)
      debug(s"got request for ${length}B from $friendNumber for file $fileNumber at $position")
      assert(length >= 0)
      if (length == 0) {
        aliceSentFileNumber = -1
        debug("finish transmission")
        state.finish
      } else {
        val nextState = state.addTask { (tox, state) =>
          debug(s"sending ${length}B to $friendNumber from position $position")
          tox.fileSendChunk(friendNumber, fileNumber, position,
            fileData.slice(position.toInt, Math.min(position.toInt + length, fileData.length)))
          state
        }
        aliceOffset += length
        if (aliceOffset >= fileData.length / 4 && aliceShouldPause == -1) {
          aliceShouldPause = 0
          nextState.addTask { (tox, state) =>
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
          if (aliceShouldPause != 0) {
            debug("bob accept file transmission request")
          } else {
            debug("see bob resume file transmission")
            aliceShouldPause = 1
          }
          state
        } else if (control == ToxFileControl.PAUSE) {
          state.addTask { (tox, state) =>
            aliceShouldPause = 0
            tox.friendSendMessage(friendNumber, ToxMessageType.NORMAL, 0,
              ToxFriendMessage.unsafeFromByteArray("Please resume the file transfer".getBytes))
            state
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
              ToxFriendMessage.unsafeFromByteArray("Please resume the file transfer".getBytes))
            state
          }
        } else {
          state
        }
      }
    }

    override def fileRecvChunk(friendNumber: Int, fileNumber: Int, position: Long, data: Array[Byte])(state: ChatState): ChatState = {
      assert(isBob)
      debug(s"receive file chunk from position $position of length ${data.length} shouldPause $bobShouldPause")
      if (data.length == 0 && receivedData.length == bobOffset) {
        assert(receivedData sameElements fileData)
        debug("finish transmission")
        state.finish
      } else {
        System.arraycopy(data, 0, receivedData, position.toInt, data.length)
        bobOffset += data.length
        if (bobOffset >= fileData.length * 2 / 4 && bobShouldPause == -1) {
          bobShouldPause = 0
          state.addTask { (tox, state) =>
            debug("send file control to pause")
            tox.fileControl(friendNumber, bobSentFileNumber, ToxFileControl.PAUSE)
            state
          }
        } else {
          state
        }
      }
    }

    override def friendMessage(friendNumber: Int, newType: ToxMessageType, timeDelta: Int, message: ToxFriendMessage)(state: ChatState): ChatState = {
      debug(s"received a message: ${new String(message.value)}")
      assert(new String(message.value) == "Please resume the file transfer")
      state.addTask { (tox, state) =>
        addFriendMessageTask(friendNumber, bobSentFileNumber, fileId, tox)
        state
      }
    }

  }

}
