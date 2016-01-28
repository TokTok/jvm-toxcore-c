package im.tox.gui.events

import java.awt.event.{ActionEvent, ActionListener}
import javax.swing._

import im.tox.gui.MainView
import im.tox.tox4j.ToxCoreTestBase.readablePublicKey
import im.tox.tox4j.core.options.{ProxyOptions, SaveDataOptions, ToxOptions}
import im.tox.tox4j.exceptions.ToxException
import im.tox.tox4j.impl.jni.ToxCoreImpl

import scala.annotation.tailrec

final class ConnectButtonOnAction(toxGui: MainView) extends ActionListener {

  private def setConnectSettingsEnabled(enabled: Boolean): Unit = {
    Seq(
      toxGui.enableIPv6CheckBox,
      toxGui.enableUdpCheckBox,
      toxGui.noneRadioButton,
      toxGui.httpRadioButton,
      toxGui.socksRadioButton,
      toxGui.proxyHost,
      toxGui.proxyPort
    ).foreach(_.setEnabled(enabled))

    Seq(
      toxGui.bootstrapHost,
      toxGui.bootstrapPort,
      toxGui.bootstrapKey,
      toxGui.bootstrapButton,
      toxGui.friendId,
      toxGui.friendRequest,
      toxGui.addFriendButton,
      toxGui.actionRadioButton,
      toxGui.messageRadioButton,
      toxGui.messageText,
      toxGui.sendButton
    ).foreach(_.setEnabled(!enabled))
  }

  private def toxOptions: ToxOptions = {
    val proxy: ProxyOptions =
      if (toxGui.httpRadioButton.isSelected) {
        ProxyOptions.Http(toxGui.proxyHost.getText, toxGui.proxyPort.getText.toInt)
      } else if (toxGui.socksRadioButton.isSelected) {
        ProxyOptions.Socks5(toxGui.proxyHost.getText, toxGui.proxyPort.getText.toInt)
      } else {
        ProxyOptions.None
      }

    val toxSave: SaveDataOptions =
      toxGui.load() match {
        case None       => SaveDataOptions.None
        case Some(data) => SaveDataOptions.ToxSave(data)
      }

    ToxOptions(
      toxGui.enableIPv6CheckBox.isSelected,
      toxGui.enableUdpCheckBox.isSelected,
      proxy = proxy,
      saveData = toxSave
    )
  }

  private def connect(): Unit = {
    try {
      toxGui.tox = new ToxCoreImpl[Unit](toxOptions)

      for (friendNumber <- toxGui.tox.getFriendList) {
        toxGui.friendListModel.add(
          friendNumber,
          toxGui.tox.getFriendPublicKey(friendNumber)
        )
      }

      toxGui.selfPublicKey.setText(readablePublicKey(toxGui.tox.getAddress.value))
      toxGui.tox.callback(toxGui.toxEvents)

      toxGui.eventLoop = new Thread(new Runnable() {
        @tailrec
        override def run(): Unit = {
          Thread.sleep(toxGui.tox.iterationInterval)
          toxGui.tox.iterate(())
          run()
        }
      })

      toxGui.eventLoop.start()
      toxGui.connectButton.setText("Disconnect")
      setConnectSettingsEnabled(false)
      toxGui.addMessage("Created Tox instance; started event loop")
    } catch {
      case e: ToxException[_] =>
        toxGui.addMessage("Error creating Tox instance: " + e.code)
      case e: Throwable =>
        JOptionPane.showMessageDialog(toxGui, MainView.printExn(e))
    }
  }

  private def disconnect(): Unit = {
    toxGui.eventLoop.interrupt()
    try {
      toxGui.tox.close()
      toxGui.tox = null
      toxGui.eventLoop.join()
      setConnectSettingsEnabled(true)
      toxGui.connectButton.setText("Connect")
      toxGui.addMessage("Disconnected")
    } catch {
      case e: InterruptedException =>
        toxGui.addMessage("Disconnect interrupted")
    }
  }

  override def actionPerformed(event: ActionEvent): Unit = {
    toxGui.connectButton.getText match {
      case "Connect" =>
        connect()
      case "Disconnect" =>
        disconnect()
    }
  }

}
