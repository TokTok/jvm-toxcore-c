package im.tox.gui.domain

import java.io.Serializable

import im.tox.tox4j.core.ToxPublicKey
import im.tox.tox4j.core.enums.{ToxConnection, ToxUserStatus}

final class Friend(val publicKey: ToxPublicKey) extends Serializable {

  var name: String = "<No name>"
  @transient
  var connectionStatus: ToxConnection = null
  @transient
  var status: ToxUserStatus = null
  var statusMessage: String = null
  @transient
  var typing: Boolean = false

  override def toString: String = {
    s"Friend ($name, $connectionStatus, $status, $statusMessage)${if (typing) " [typing]" else ""}"
  }

}
