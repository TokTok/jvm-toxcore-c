package im.tox.tox4j

import im.tox.tox4j.av.SamplingRate
import im.tox.tox4j.core.callbacks.ToxEventListener
import im.tox.tox4j.core.enums.ToxConnection
import org.jetbrains.annotations.NotNull

final class ConnectedListener extends ToxEventListener[Unit] {

  @NotNull private var value = ToxConnection.NONE

  override def selfConnectionStatus(@NotNull connectionStatus: ToxConnection)(state: Unit): Unit = {
    value = connectionStatus
  }

  def isConnected: Boolean = value != ToxConnection.NONE

}
