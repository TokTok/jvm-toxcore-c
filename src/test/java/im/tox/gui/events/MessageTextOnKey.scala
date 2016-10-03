package im.tox.gui.events

import java.awt.event.{ KeyAdapter, KeyEvent }

import im.tox.gui.MainView

final class MessageTextOnKey(toxGui: MainView) extends KeyAdapter {

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  override def keyPressed(event: KeyEvent): Unit = {
    if (event.getKeyChar == '\n') {
      toxGui.sendButton.doClick()
    }
  }

}
