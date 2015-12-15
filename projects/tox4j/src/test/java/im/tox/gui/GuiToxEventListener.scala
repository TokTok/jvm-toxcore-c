package im.tox.gui

import javax.swing._

import im.tox.tox4j.ToxCoreTestBase.readablePublicKey
import im.tox.tox4j.core.callbacks.ToxCoreEventListener
import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums.{ToxConnection, ToxFileControl, ToxMessageType, ToxUserStatus}
import org.jetbrains.annotations.NotNull

final class GuiToxEventListener(toxGui: MainView) extends ToxCoreEventListener[Unit] {

  private def addMessage(method: String, args: Any*): Unit = {
    val str = new StringBuilder
    str.append(method)
    str.append('(')
    var first = true
    for (arg <- args) {
      if (!first) {
        str.append(", ")
      }
      str.append(arg)
      first = false
    }
    str.append(')')
    toxGui.addMessage(str.toString())
  }

  override def selfConnectionStatus(@NotNull connectionStatus: ToxConnection)(state: Unit): Unit = {
    addMessage("selfConnectionStatus", connectionStatus)
  }

  override def fileRecvControl(friendNumber: ToxFriendNumber, fileNumber: Int, @NotNull control: ToxFileControl)(state: Unit): Unit = {
    addMessage("fileRecvControl", friendNumber, fileNumber, control)

    try {
      control match {
        case ToxFileControl.RESUME =>
          toxGui.fileModel.get(friendNumber, fileNumber).resume()
        case ToxFileControl.CANCEL =>
          throw new UnsupportedOperationException("CANCEL")
        case ToxFileControl.PAUSE =>
          throw new UnsupportedOperationException("PAUSE")
      }
    } catch {
      case e: Throwable =>
        JOptionPane.showMessageDialog(toxGui, MainView.printExn(e))
    }
  }

  override def fileRecv(friendNumber: ToxFriendNumber, fileNumber: Int, kind: Int, fileSize: Long, @NotNull filename: ToxFilename)(state: Unit): Unit = {
    addMessage("fileRecv", friendNumber, fileNumber, kind, fileSize, new String(filename.value))

    try {
      val confirmation = JOptionPane.showConfirmDialog(toxGui, "Incoming file transfer: " + new String(filename.value))

      val cancel =
        if (confirmation == JOptionPane.OK_OPTION) {
          val chooser = new JFileChooser
          val returnVal = chooser.showOpenDialog(toxGui)

          if (returnVal == JFileChooser.APPROVE_OPTION) {
            toxGui.fileModel.addIncoming(friendNumber, fileNumber, kind, fileSize, chooser.getSelectedFile)
            toxGui.tox.fileControl(friendNumber, fileNumber, ToxFileControl.RESUME)
            true
          } else {
            false
          }
        } else {
          false
        }

      if (cancel) {
        toxGui.tox.fileControl(friendNumber, fileNumber, ToxFileControl.CANCEL)
      }
    } catch {
      case e: Throwable =>
        JOptionPane.showMessageDialog(toxGui, MainView.printExn(e))
    }
  }

  override def fileRecvChunk(friendNumber: ToxFriendNumber, fileNumber: Int, position: Long, @NotNull data: Array[Byte])(state: Unit): Unit = {
    addMessage("fileRecvChunk", friendNumber, fileNumber, position, "byte[" + data.length + ']')
    try {
      toxGui.fileModel.get(friendNumber, fileNumber).write(position, data)
    } catch {
      case e: Throwable =>
        JOptionPane.showMessageDialog(toxGui, MainView.printExn(e))
    }
  }

  override def fileChunkRequest(friendNumber: ToxFriendNumber, fileNumber: Int, position: Long, length: Int)(state: Unit): Unit = {
    addMessage("fileChunkRequest", friendNumber, fileNumber, position, length)
    try {
      if (length == 0) {
        toxGui.fileModel.remove(friendNumber, fileNumber)
      } else {
        toxGui.tox.fileSendChunk(friendNumber, fileNumber, position, toxGui.fileModel.get(friendNumber, fileNumber).read(position, length))
      }
    } catch {
      case e: Throwable =>
        JOptionPane.showMessageDialog(toxGui, MainView.printExn(e))
    }
  }

  override def friendConnectionStatus(friendNumber: ToxFriendNumber, @NotNull connectionStatus: ToxConnection)(state: Unit): Unit = {
    addMessage("friendConnectionStatus", friendNumber, connectionStatus)
    toxGui.friendListModel.setConnectionStatus(friendNumber, connectionStatus)
  }

  override def friendLosslessPacket(friendNumber: ToxFriendNumber, @NotNull data: ToxLosslessPacket)(state: Unit): Unit = {
    addMessage("friendLosslessPacket", friendNumber, readablePublicKey(data.value))
  }

  override def friendLossyPacket(friendNumber: ToxFriendNumber, @NotNull data: ToxLossyPacket)(state: Unit): Unit = {
    addMessage("friendLossyPacket", friendNumber, readablePublicKey(data.value))
  }

  override def friendMessage(friendNumber: ToxFriendNumber, @NotNull messageType: ToxMessageType, timeDelta: Int, @NotNull message: ToxFriendMessage)(state: Unit): Unit = {
    addMessage("friendMessage", friendNumber, messageType, timeDelta, new String(message.value))
  }

  override def friendName(friendNumber: ToxFriendNumber, @NotNull name: ToxNickname)(state: Unit): Unit = {
    addMessage("friendName", friendNumber, new String(name.value))
    toxGui.friendListModel.setName(friendNumber, new String(name.value))
  }

  override def friendRequest(@NotNull publicKey: ToxPublicKey, timeDelta: Int, @NotNull message: ToxFriendRequestMessage)(state: Unit): Unit = {
    addMessage("friendRequest", readablePublicKey(publicKey.value), timeDelta, new String(message.value))
  }

  override def friendStatus(friendNumber: ToxFriendNumber, @NotNull status: ToxUserStatus)(state: Unit): Unit = {
    addMessage("friendStatus", friendNumber, status)
    toxGui.friendListModel.setStatus(friendNumber, status)
  }

  override def friendStatusMessage(friendNumber: ToxFriendNumber, @NotNull message: ToxStatusMessage)(state: Unit): Unit = {
    addMessage("friendStatusMessage", friendNumber, new String(message.value))
    toxGui.friendListModel.setStatusMessage(friendNumber, new String(message.value))
  }

  override def friendTyping(friendNumber: ToxFriendNumber, isTyping: Boolean)(state: Unit): Unit = {
    addMessage("friendTyping", friendNumber, isTyping)
    toxGui.friendListModel.setTyping(friendNumber, isTyping)
  }

  override def friendReadReceipt(friendNumber: ToxFriendNumber, messageId: Int)(state: Unit): Unit = {
    addMessage("friendReadReceipt", friendNumber, messageId)
  }
}
