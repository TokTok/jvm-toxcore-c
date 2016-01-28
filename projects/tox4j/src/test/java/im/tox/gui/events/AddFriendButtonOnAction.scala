package im.tox.gui.events

import java.awt.event.{ActionEvent, ActionListener}
import javax.swing._

import im.tox.gui.MainView
import im.tox.tox4j.ToxCoreTestBase.parsePublicKey
import im.tox.tox4j.core._
import im.tox.tox4j.core.exceptions.ToxFriendAddException

final class AddFriendButtonOnAction(toxGui: MainView) extends ActionListener {

  override def actionPerformed(event: ActionEvent): Unit = {
    try {
      val publicKey = parsePublicKey(toxGui.friendId.getText)

      val friendNumber =
        if (toxGui.friendRequest.getText.isEmpty) {
          toxGui.tox.addFriendNorequest(ToxPublicKey.unsafeFromByteArray(publicKey))
        } else {
          toxGui.tox.addFriend(
            ToxFriendAddress.unsafeFromByteArray(publicKey),
            ToxFriendRequestMessage.unsafeFromByteArray(toxGui.friendRequest.getText.getBytes)
          )
        }

      toxGui.friendListModel.add(
        friendNumber,
        ToxPublicKey.unsafeFromByteArray(publicKey.slice(0, ToxCoreConstants.PublicKeySize))
      )
      toxGui.addMessage("Added friend number ", friendNumber)
      toxGui.save()
    } catch {
      case e: ToxFriendAddException =>
        toxGui.addMessage("Add friend failed: ", e.code)
      case e: Throwable =>
        JOptionPane.showMessageDialog(toxGui, MainView.printExn(e))
    }
  }

}
