package im.tox.client

import java.io.PrintWriter

import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.core.ToxCore

final case class ToxClient(
    tox: ToxCore[ToxClientState],
    av: ToxAv[ToxClientState],
    state: ToxClientState
) {

  def printInfo(out: PrintWriter, id: Int): Unit = {
    out.print("Instance "); out.print(id); out.print(" (connection = "); out.print(state.connection); out.println("):")
    out.print("  Name:           "); out.println(state.profile.name)
    out.print("  Status message: "); out.println(state.profile.statusMessage)
    out.print("  Status:         "); out.println(state.profile.status)
    out.print("  Friend address: "); out.println(state.address.toHexString)
    out.print("  DHT public key: "); out.println(state.dhtId.toHexString)
    out.print("  UDP port:       "); out.println(state.udpPort)
    out.println("  Friends:")
    for ((friendNumber, friend) <- state.friends) {
      out.print("    "); out.print(friendNumber); out.print(" -> "); out.println(friend)
    }
  }

}
