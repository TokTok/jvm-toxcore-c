package im.tox.core.dht.packets.dht

import im.tox.core.network.PacketKind

object PingRequestPacket extends PingPacketCompanion(PacketKind.PingRequest) {
  override def isResponse: Boolean = false
}
