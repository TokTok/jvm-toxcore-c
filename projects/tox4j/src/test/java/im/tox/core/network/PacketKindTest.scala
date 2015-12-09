package im.tox.core.network

import im.tox.core.typesafe.EnumModuleCompanionTest
import im.tox.tox4j.EnumerationMacros.sealedInstancesOf
import im.tox.tox4j.testing.{CheckedOrderingEq, CountingOrdering}
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

final class PacketKindTest extends EnumModuleCompanionTest(PacketKind) {

  override val arbT = PacketKindTest.arbPacketKind

  test("enumeration value count") {
    // XXX: update the number 20 when adding a packet kind
    assert(PacketKind.values.size == 20)
    // Run sealedInstancesOf again, because inside the module definition, order matters. Here it doesn't.
    assert(PacketKind.values == sealedInstancesOf[PacketKind])
  }

  test("all packet IDs are unique") {
    // Shadow the original ordering with one that validates that !(a < b) && !(b < a) => a eq b.
    // This tests physical equality (Java == as opposed to .equals).
    implicit val ordPacketKind: Ordering[PacketKind] = CheckedOrderingEq(PacketKind.ordT)
    val values = sealedInstancesOf[PacketKind]
    assert(values.nonEmpty)
    assert(values.forall { a =>
      (values - a).forall(_.id != a.id)
    })
  }

  test(s"locally overriding the $PacketKind ordering") {
    implicit val ordPacketKind: CountingOrdering[PacketKind] = CountingOrdering(PacketKind.ordT)
    assert(sealedInstancesOf[PacketKind].nonEmpty)
    assert(ordPacketKind.count == 68) // Assuming the 20 values are inserted in alphabetic order
  }

}
