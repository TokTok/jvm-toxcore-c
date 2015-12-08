package im.tox.core.network

import im.tox.core.ModuleCompanionTest
import org.scalacheck.{Arbitrary, Gen}

object PacketKindTest {

  implicit val arbPacketKind: Arbitrary[PacketKind] =
    Arbitrary(
      Gen.oneOf(
        // A pre-defined list of packet kinds in case the enumeration macro is broken.
        Gen.oneOf(
          PacketKind.PingRequest,
          PacketKind.PingResponse,
          PacketKind.NodesRequest,
          PacketKind.NodesResponse,
          PacketKind.DhtRequest
        ),
        Gen.oneOf(PacketKind.values.toSeq)
      )
    )

}

final class PacketKindTest extends ModuleCompanionTest(PacketKind) {

  override val arbT = PacketKindTest.arbPacketKind

  test("enumeration values") {
    // XXX: update the number 20 when adding a packet kind
    assert(PacketKind.values.size == 20)
  }

}
