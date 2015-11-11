package im.tox.core.network

import im.tox.core.ModuleCompanionTest
import org.scalacheck.{Arbitrary, Gen}

object PacketKindTest {

  implicit val arbPacketKind: Arbitrary[PacketKind] =
    Arbitrary(
      Gen.oneOf(
        PacketKind.PingRequest,
        PacketKind.PingResponse,
        PacketKind.NodesRequest,
        PacketKind.NodesResponse,
        PacketKind.DhtRequest
      )
    )

}

final class PacketKindTest extends ModuleCompanionTest(PacketKind) {

  override val arbT = PacketKindTest.arbPacketKind

}
