package im.tox.client.commands

import im.tox.client.{HostInfo, Say, ToxClientState}
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.data.{ToxFriendMessage, ToxFriendNumber}
import im.tox.tox4j.core.enums.ToxMessageType
import im.tox.tox4j.testing.GetDisjunction._

object ShowCommandHandler extends Say {

  def apply(friendNumber: ToxFriendNumber, state: ToxClientState, request: String): ToxClientState = {
    def const(response: String)(tox: ToxCore): String = response

    val response: (ToxCore => String) = request match {
      case "address" => _.getAddress.toString
      case "dhtid"   => _.getDhtId.toString
      case "udpport" => _.getUdpPort.toString
      case "ipv4"    => const(HostInfo.ipv4.toString)
      case "ipv6"    => const(HostInfo.ipv6.toString)
      case other     => const(s"I don't know about '$other'")
    }

    state.addTask { (tox, av, state) =>
      tox.friendSendMessage(friendNumber, ToxMessageType.NORMAL, 0, ToxFriendMessage.fromString(response(tox)).get)
      state
    }
  }

}
