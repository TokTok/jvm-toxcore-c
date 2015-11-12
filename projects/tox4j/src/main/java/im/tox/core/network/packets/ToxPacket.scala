package im.tox.core.network.packets

import im.tox.core.ModuleCompanion
import im.tox.core.crypto.PlainText
import im.tox.core.dht.packets.DhtRequestPacket
import im.tox.core.network.PacketKind
import im.tox.core.typesafe.Security

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
  payload: PlainText[Security.NonSensitive]
)

object ToxPacket extends ModuleCompanion[ToxPacket[PacketKind], Security.NonSensitive] {

  override val codec =
    (PacketKind.codec ~ PlainText.codec).xmap[ToxPacket[PacketKind]](
      { case (kind, payload) => ToxPacket(kind, payload) },
      { case ToxPacket(kind, payload) => (kind, payload) }
    )

}
