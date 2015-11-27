package im.tox.gui.events

import java.awt.event.{ActionEvent, ActionListener}
import javax.swing._

import im.tox.core.network.Port
import im.tox.gui.MainView
import im.tox.tox4j.ToxCoreTestBase.parsePublicKey
import im.tox.tox4j.core.data.ToxPublicKey
import im.tox.tox4j.core.exceptions.ToxBootstrapException
import im.tox.tox4j.testing.GetDisjunction._

final class BootstrapButtonOnAction(toxGui: MainView) extends ActionListener {

  def actionPerformed(event: ActionEvent): Unit = {
    try {
      Seq(
        toxGui.tox.addTcpRelay _,
        toxGui.tox.bootstrap _
      ) foreach (_(
        toxGui.bootstrapHost.getText,
        Port.unsafeFromInt(toxGui.bootstrapPort.getText.toInt),
        ToxPublicKey.fromValue(parsePublicKey(toxGui.bootstrapKey.getText.trim)).get
      ))
    } catch {
      case e: ToxBootstrapException =>
        toxGui.addMessage("Bootstrap failed: ", e.code)
      case e: Throwable =>
        JOptionPane.showMessageDialog(toxGui, MainView.printExn(e))
    }
  }

}
