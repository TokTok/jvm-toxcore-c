package im.tox.tox4j.core.callbacks

import java.util.Random

import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums.{ToxConnection, ToxFileControl, ToxFileKind}
import im.tox.tox4j.testing.autotest.{AliceBobTest, AliceBobTestBase}

/**
 * This test intends to simulate the situation of resuming a file
 * transmission after deleting and re-adding a friend.
 * - Alice initiated the file transmission and Bob accepted
 * - Alice paused the file transmission after sending 1/10 of the file
 *   and deleted Bob from the friend list
 * - Bob saw Alice went offline and sent a friend request to Alice
 * - Alice accepted the friend request and resumed the file transmission
 * - Bob received all the file data
 */
final class FileResumeAfterRestartTest extends AliceBobTest {

  override type State = Unit
  override def initialState: State = ()

  private val fileData = new Array[Byte](13710)
  private var aliceAddress = ToxFriendAddress.unsafeFromValue(Array.empty)
  new Random().nextBytes(fileData)

  protected override def newChatClient(name: String, expectedFriendName: String) = new Alice(name, expectedFriendName)

  final class Alice(name: String, expectedFriendName: String) extends ChatClient(name, expectedFriendName) {

    private var aliceOffset = 0L
    private var fileId = ToxFileId.empty
    private var aliceSentFileNumber = -1
    private var aliceShouldPause = -1
    private val receivedData = new Array[Byte](fileData.length)
    private var bobSentFileNumber = -1
    private var bobOffset = 0L
    private var selfPublicKey = ToxPublicKey.unsafeFromValue(Array.empty)

    override def friendRequest(publicKey: ToxPublicKey, timeDelta: Int, message: ToxFriendRequestMessage)(state: ChatState): ChatState = {
      assert(isAlice)
      state.addTask { (tox, av, state) =>
        debug("accept Bob's friend request")
        tox.addFriendNorequest(publicKey)
        aliceShouldPause = 1
        state
      }
    }

    override def friendConnectionStatus(friendNumber: ToxFriendNumber, connection: ToxConnection)(state: ChatState): ChatState = {
      if (isAlice) {
        if (connection != ToxConnection.NONE) {
          debug(s"is now connected to friend $friendNumber")
          assert(friendNumber == AliceBobTestBase.FriendNumber)
          debug(s"initiate file sending to friend $friendNumber")
          state.addTask { (tox, av, state) =>
            aliceSentFileNumber = tox.fileSend(
              friendNumber,
              ToxFileKind.DATA,
              fileData.length,
              ToxFileId.empty,
              ToxFilename.fromValue(s"file for $expectedFriendName.png".getBytes).get
            )
            fileId = tox.getFileFileId(friendNumber, aliceSentFileNumber)
            aliceAddress = tox.getAddress
            state
          }
        } else {
          state
        }
      } else {
        if (connection != ToxConnection.NONE) {
          debug(s"is now connected to friend $friendNumber")
          assert(friendNumber == AliceBobTestBase.FriendNumber)
          state
        } else {
          debug("See alice go off-line")
          state.addTask { (tox, av, state) =>
            tox.deleteFriend(friendNumber)
            tox.addFriend(aliceAddress, ToxFriendRequestMessage.fromValue("Please add me back".getBytes).get)
            state
          }
        }
      }
    }

    override def fileChunkRequest(friendNumber: ToxFriendNumber, fileNumber: Int, position: Long, length: Int)(state: ChatState): ChatState = {
      assert(isAlice)
      debug(s"got request for ${length}B from $friendNumber for file $fileNumber at $position")
      assert(length >= 0)
      if (length == 0) {
        aliceSentFileNumber = -1
        debug("finish transmission")
        state.finish
      } else {
        if (aliceShouldPause != 0) {
          val nextState = state.addTask { (tox, av, state) =>
            debug(s"sending $length B to $friendNumber from position $position")
            tox.fileSendChunk(friendNumber, fileNumber, position,
              fileData.slice(position.toInt, Math.min(position.toInt + length, fileData.length)))
            state
          }
          aliceOffset += length
          if (aliceOffset >= fileData.length / 10 && aliceShouldPause == -1) {
            aliceShouldPause = 0
            nextState.addTask { (tox, av, state) =>
              debug("pause file transmission")
              tox.fileControl(friendNumber, fileNumber, ToxFileControl.PAUSE)
              tox.deleteFriend(friendNumber)
              state
            }
          } else {
            nextState
          }
        } else {
          state
        }
      }
    }

    override def fileRecv(friendNumber: ToxFriendNumber, fileNumber: Int, kind: Int, fileSize: Long, filename: ToxFilename)(state: ChatState): ChatState = {
      assert(isBob)
      debug(s"received file send request $fileNumber from friend number $friendNumber current offset $bobOffset")
      assert(friendNumber == AliceBobTestBase.FriendNumber)
      assert(kind == ToxFileKind.DATA)
      assert(new String(filename.value) == s"file for $name.png")
      bobSentFileNumber = fileNumber
      state.addTask { (tox, av, state) =>
        selfPublicKey = tox.getPublicKey
        debug(s"sending control RESUME for $fileNumber")
        debug(s"seek file to $bobOffset")
        tox.fileSeek(friendNumber, bobSentFileNumber, bobOffset)
        tox.fileControl(friendNumber, fileNumber, ToxFileControl.RESUME)
        state
      }
    }

    override def fileRecvChunk(friendNumber: ToxFriendNumber, fileNumber: Int, position: Long, data: Array[Byte])(state: ChatState): ChatState = {
      assert(isBob)
      debug(s"receive file chunk from position $position of length ${data.length}")
      if (data.length == 0 && receivedData.length == bobOffset) {
        assert(receivedData sameElements fileData)
        debug("finish transmission")
        state.finish
      } else {
        System.arraycopy(data, 0, receivedData, position.toInt, data.length)
        bobOffset += data.length
        state
      }
    }

  }

}
