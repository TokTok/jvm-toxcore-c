package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.{ToxFileId, ToxFilename}
import im.tox.tox4j.core.enums.{ToxConnection, ToxFileKind}
import im.tox.tox4j.core.{ToxCore, ToxCoreConstants}
import im.tox.tox4j.testing.autotest.{AliceBobTest, AliceBobTestBase}

final class FileRecvCallbackTest extends AliceBobTest {

  override type State = Seq[Byte]
  override def initialState: State = Nil

  protected override def newChatClient(name: String, expectedFriendName: String) = new ChatClient(name, expectedFriendName) {

    override def setup(tox: ToxCore[ChatState])(state: ChatState): ChatState = {
      if (isAlice) {
        state.set("This is a file for Bob".getBytes)
      } else {
        state.set("This is a file for Alice".getBytes)
      }
    }

    override def friendConnectionStatus(friendNumber: Int, connectionStatus: ToxConnection)(state: ChatState): ChatState = {
      super.friendConnectionStatus(friendNumber, connectionStatus)(state)
      if (connectionStatus != ToxConnection.NONE) {
        assert(friendNumber == AliceBobTestBase.FriendNumber)
        state.addTask { (tox, state) =>
          tox.fileSend(
            friendNumber,
            ToxFileKind.DATA,
            state.get.length,
            ToxFileId.empty,
            ToxFilename.fromValue(s"file for $expectedFriendName.png".getBytes).get
          )
          state
        }
      } else {
        state
      }
    }

    override def fileRecv(friendNumber: Int, fileNumber: Int, kind: Int, fileSize: Long, filename: ToxFilename)(state: ChatState): ChatState = {
      debug("received file send request " + fileNumber + " from friend number " + friendNumber)
      assert(friendNumber == AliceBobTestBase.FriendNumber)
      assert(fileNumber == (0 | 0x10000))
      assert(kind == ToxFileKind.DATA)
      if (isAlice) {
        assert(fileSize == "This is a file for Alice".length)
      } else {
        assert(fileSize == "This is a file for Bob".length)
      }
      assert(new String(filename.value) == s"file for $selfName.png")
      state.addTask { (tox, state) =>
        val fileId = tox.getFileFileId(friendNumber, fileNumber)
        assert(fileId.value != null)
        assert(fileId.value.length == ToxCoreConstants.FileIdLength)
        state.finish
      }
    }
  }
}
