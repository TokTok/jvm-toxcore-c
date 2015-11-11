package im.tox.core.network.packets

import im.tox.core.ModuleCompanion
import im.tox.core.crypto.PlainText
import im.tox.core.dht.packets.DhtRequestPacket
import im.tox.core.network.PacketKind

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

  override val codec =
    (PacketKind.codec ~ PlainText.codec).xmap[ToxPacket[PacketKind]](
      { case (kind, payload) => ToxPacket(kind, payload) },
      { case ToxPacket(kind, payload) => (kind, payload) }
    )

}
