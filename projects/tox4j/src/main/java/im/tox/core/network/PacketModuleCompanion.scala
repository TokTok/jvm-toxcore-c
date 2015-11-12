package im.tox.core.network

import im.tox.core.ModuleCompanion
import im.tox.core.typesafe.Security

abstract class PacketModuleCompanion[T, Kind <: PacketKind, S <: Security](val packetKind: Kind)
    extends ModuleCompanion[T, S] {
  type PacketKind = Kind
}
