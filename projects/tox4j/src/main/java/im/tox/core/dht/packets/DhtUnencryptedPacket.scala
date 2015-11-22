package im.tox.core.dht.packets

import im.tox.core.ModuleCompanion
import im.tox.core.typesafe.Security
import scodec.codecs._

final case class DhtUnencryptedPacket[Payload](
  payload: Payload,
  pingId: Long
)

object DhtUnencryptedPacket {

  final case class Make[Payload, S <: Security](module: ModuleCompanion[Payload, S])
      extends ModuleCompanion[DhtUnencryptedPacket[Payload], S] {

    override val codec =
      (module.codec ~ int64).xmap[DhtUnencryptedPacket[Payload]](
        { case (payload, pingId) => DhtUnencryptedPacket(payload, pingId) },
        { case DhtUnencryptedPacket(payload, pingId) => (payload, pingId) }
      )

  }

}
