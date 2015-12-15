package im.tox.gui.events

import java.awt.event.{ActionEvent, ActionListener}
import javax.swing._

import im.tox.gui.MainView
import im.tox.tox4j.core.data.{ToxFriendNumber, ToxFriendMessage}
import im.tox.tox4j.core.enums.ToxMessageType
import im.tox.tox4j.core.exceptions.ToxFriendSendMessageException
import im.tox.tox4j.testing.GetDisjunction._

final class SendButtonOnAction(toxGui: MainView) extends ActionListener {

  override def actionPerformed(event: ActionEvent): Unit = {
    try {
      val friendNumber = {
        val index = toxGui.friendList.getSelectedIndex
        if (index == -1) {
          JOptionPane.showMessageDialog(toxGui, "Select a friend to send a message to")
        }
        ToxFriendNumber.fromInt(index).get // TODO(iphydf): This fails if no friend is selected.
      }

      if (toxGui.messageRadioButton.isSelected) {
        toxGui.tox.friendSendMessage(friendNumber, ToxMessageType.NORMAL, 0,
          ToxFriendMessage.fromString(toxGui.messageText.getText).get)
        toxGui.addMessage("Sent message to ", s"$friendNumber: ${toxGui.messageText.getText}")
      } else if (toxGui.actionRadioButton.isSelected) {
        toxGui.tox.friendSendMessage(friendNumber, ToxMessageType.ACTION, 0,
          ToxFriendMessage.fromString(toxGui.messageText.getText).get)
        toxGui.addMessage("Sent action to ", s"$friendNumber: ${toxGui.messageText.getText}")
      }

      toxGui.messageText.setText("")
    } catch {
      case e: ToxFriendSendMessageException =>
        toxGui.addMessage("Send message failed: ", e.code)
      case e: Throwable =>
        JOptionPane.showMessageDialog(toxGui, MainView.printExn(e))
    }
  }

}
