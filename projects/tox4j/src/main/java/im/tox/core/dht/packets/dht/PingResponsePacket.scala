package im.tox.core.dht.packets.dht

import im.tox.core.network.PacketKind

object PingResponsePacket extends PingPacketCompanion(PacketKind.PingResponse) {
  override def isResponse: Boolean = true
}
