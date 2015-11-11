package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.enums.{ToxFileControl, ToxFileKind}
import im.tox.tox4j.core.{ToxCore, ToxFileId, ToxFilename}

final class FilePauseResumeWithResendTest extends FilePauseResumeTestBase {

  protected override def newChatClient(name: String, expectedFriendName: String) = new Alice(name, expectedFriendName)

  final class Alice(name: String, expectedFriendName: String) extends super.Alice(name, expectedFriendName) {

    protected override def addFileRecvTask(
      friendNumber: Int,
      fileNumber: Int,
      bobSentFileNumber: Int,
      bobOffset: Long,
      tox: ToxCore[ChatState]
    ): Unit = {
      debug(s"seek file to $bobOffset")
      tox.fileSeek(friendNumber, bobSentFileNumber, bobOffset)
      bobShouldPause = 1
      debug(s"sending control RESUME for $fileNumber")
      tox.fileControl(friendNumber, fileNumber, ToxFileControl.RESUME)
    }

    protected override def addFriendMessageTask(
      friendNumber: Int,
      bobSentFileNumber: Int,
      fileId: ToxFileId,
      tox: ToxCore[ChatState]
    ): Unit = {
      if (isAlice) {
        aliceSentFileNumber = tox.fileSend(
          friendNumber,
          ToxFileKind.DATA,
          fileData.length,
          fileId,
          ToxFilename.unsafeFromByteArray(("file for " + expectedFriendName + ".png").getBytes)
        )
        aliceShouldPause = 1
      } else {
        debug("Send resume file transmission control")
        tox.fileControl(friendNumber, bobSentFileNumber, ToxFileControl.RESUME)
      }
    }

  }

}
