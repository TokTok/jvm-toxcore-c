package im.tox.gui.events

import java.awt.event.{ActionEvent, ActionListener}
import java.io.File
import javax.swing._

import im.tox.gui.MainView
import im.tox.tox4j.core.data.{ToxFriendNumber, ToxFileId, ToxFilename}
import im.tox.tox4j.core.enums.ToxFileKind
import im.tox.tox4j.core.exceptions.ToxFileSendException
import im.tox.tox4j.testing.GetDisjunction._

final class SendFileButtonOnAction(toxGui: MainView) extends ActionListener {

  override def actionPerformed(event: ActionEvent): Unit = {
    try {
      val friendNumber = {
        val index = toxGui.friendList.getSelectedIndex
        if (index == -1) {
          JOptionPane.showMessageDialog(toxGui, "Select a friend to send a message to")
        }
        ToxFriendNumber.fromInt(index).get // TODO(iphydf): This fails if no friend is selected.
      }

      val file = new File(toxGui.fileName.getText)
      if (!file.exists) {
        JOptionPane.showMessageDialog(toxGui, "File does not exist: " + file)
      } else {
        toxGui.fileModel.addOutgoing(
          friendNumber,
          file,
          toxGui.tox.fileSend(
            friendNumber,
            ToxFileKind.DATA,
            file.length,
            ToxFileId.empty,
            ToxFilename.fromString(file.getName).get
          )
        )
      }
    } catch {
      case e: ToxFileSendException =>
        toxGui.addMessage("Send file failed: ", e.code)
      case e: Throwable =>
        JOptionPane.showMessageDialog(toxGui, MainView.printExn(e))
    }
  }

}
