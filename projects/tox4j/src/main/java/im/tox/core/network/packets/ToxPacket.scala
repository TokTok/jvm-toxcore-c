package im.tox.core.network.packets

import java.io.{DataInputStream, DataOutput}

import im.tox.core.ModuleCompanion
import im.tox.core.crypto.PlainText
import im.tox.core.dht.packets.DhtRequestPacket
import im.tox.core.error.DecoderError
import im.tox.core.network.PacketKind

import scalaz.\/

/**
 * The outer packet structure. There are no further outer layers within the
 * Tox protocol. This packet sits directly inside the transport packet (e.g.
 * UDP or TCP or in a [[DhtRequestPacket]]).
 *
 * @param kind The identifier for the packet kind.
 * @param payload Data in the packet.
 */
final case class ToxPacket[+Kind <: PacketKind](
  kind: Kind,
  payload: PlainText
)

object ToxPacket extends ModuleCompanion[ToxPacket[PacketKind]] {

  override def write(self: ToxPacket[PacketKind], packetData: DataOutput): Unit = {
    PacketKind.write(self.kind, packetData)
    PlainText.write(self.payload, packetData)
  }

  override def read(packetData: DataInputStream): DecoderError \/ ToxPacket[PacketKind] = {
    for {
      kind <- PacketKind.read(packetData)
      payload <- PlainText.read(packetData)
    } yield {
      ToxPacket(
        kind,
        payload
      )
    }
  }

}
