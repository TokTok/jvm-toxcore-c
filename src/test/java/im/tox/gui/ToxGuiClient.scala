package im.tox.gui

import javax.swing._

/**
 * Run a Tox GUI client with Nimbus L&amp;F.
 */
object ToxGuiClient extends App {

  for {
    info <- UIManager.getInstalledLookAndFeels
    if info.getName == "Nimbus"
  } {
    UIManager.setLookAndFeel(info.getClassName)
  }

  val dialog = new MainView
  dialog.pack()
  dialog.setVisible(true)

}
