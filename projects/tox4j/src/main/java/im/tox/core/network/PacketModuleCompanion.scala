package im.tox.core.network

import im.tox.core.ModuleCompanion

abstract class PacketModuleCompanion[T, Kind <: PacketKind](val packetKind: Kind) extends ModuleCompanion[T] {
  type PacketKind = Kind
}
