package im.tox.toktok.app

import android.util.Log
import im.tox.tox4j.ToxEventListener
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.enums.ToxConnection

import scala.annotation.tailrec

/**
 * TODO(iphydf): Write comments.
 */
object Tox4jMain {

  // 30 seconds (20 iterations per second).
  val IterationTimeout = 60 * 20

  object IsConnectedListener extends ToxEventListener[Boolean] {
    override def selfConnectionStatus(connectionStatus: ToxConnection)(state: Boolean): Boolean = {
      Log.d(s"$Tox4jMain", s"Connection status: $connectionStatus")
      connectionStatus != ToxConnection.NONE
    }
  }

  @tailrec
  def waitForConnection(tox1: ToxCore, tox2: ToxCore, counter: Int = 0, connected: Boolean = false): Boolean = {
    if (counter >= IterationTimeout || connected) {
      connected
    } else {
      Thread.sleep(tox1.iterationInterval min tox2.iterationInterval)
      waitForConnection(
        tox1, tox2, counter + 1,
        tox1.iterate(IsConnectedListener)(connected) || tox1.iterate(IsConnectedListener)(connected)
      )
    }
  }

}
