package im.tox.core.dht.packets.dht

import im.tox.core.ModuleCompanionTest
import im.tox.core.crypto.PublicKey
import im.tox.core.crypto.PublicKeyTest._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

object NodesRequestPacketTest {

  implicit val arbNodesRequest: Arbitrary[NodesRequestPacket] =
    Arbitrary(Gen.zip(
      arbitrary[PublicKey],
      arbitrary[Long]
    ).map {
      case (requestedNode, pingId) =>
        NodesRequestPacket(requestedNode, pingId)
    })

}

final class NodesRequestPacketTest extends ModuleCompanionTest(NodesRequestPacket) {

  override val arbT = NodesRequestPacketTest.arbNodesRequest

}
