package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.{ToxFileId, ToxCore}
import im.tox.tox4j.core.enums.ToxFileControl

final class FilePauseResumeWithControlTest extends FilePauseResumeTestBase {

  protected override def newChatClient(name: String, expectedFriendName: String) = new Alice(name, expectedFriendName)

  final class Alice(name: String, expectedFriendName: String) extends super.Alice(name, expectedFriendName) {

    protected override def addFriendMessageTask(
      friendNumber: Int,
      bobSentFileNumber: Int,
      fileId: ToxFileId,
      tox: ToxCore[ChatState]
    ): Unit = {
      debug("send resume control")
      if (isBob) {
        tox.fileControl(friendNumber, bobSentFileNumber, ToxFileControl.RESUME)
        bobShouldPause = 1
      } else if (isAlice) {
        tox.fileControl(friendNumber, aliceSentFileNumber, ToxFileControl.RESUME)
        aliceShouldPause = 1
      }
    }

    protected override def addFileRecvTask(
      friendNumber: Int,
      fileNumber: Int,
      bobSentFileNumber: Int,
      bobOffset: Long,
      tox: ToxCore[ChatState]
    ): Unit = {
      debug(s"sending control RESUME for $fileNumber")
      tox.fileControl(friendNumber, fileNumber, ToxFileControl.RESUME)
    }

  }

}
