package im.tox.core.dht.packets.dht

import im.tox.core.ModuleCompanionTest
import im.tox.core.network.PacketKind
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._

object PingPacketTest {

  implicit val arbPingPacket: Arbitrary[PingPacket] =
    Arbitrary(arbitrary[Long].map(PingPacket))

}

abstract class PingPacketTest[Kind <: PacketKind](module: PingPacketCompanion[Kind]) extends ModuleCompanionTest(module) {

  final override val arbT = PingPacketTest.arbPingPacket

}
