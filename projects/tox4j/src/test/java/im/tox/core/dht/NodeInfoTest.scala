package im.tox.core.dht

import java.net.InetSocketAddress

import im.tox.core.ModuleCompanionTest
import im.tox.core.crypto.PlainText.Conversions._
import im.tox.core.crypto.PublicKey
import im.tox.core.crypto.PublicKeyTest._
import im.tox.core.dht.ProtocolTest._
import im.tox.core.network.Port
import im.tox.core.network.PortTest._
import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}

object NodeInfoTest {

  implicit val arbInetSocketAddress: Arbitrary[InetSocketAddress] =
    Arbitrary(Gen.zip(arbitrary[Boolean], Gen.choose(1, 0xffff)).map {
      case (isIpv4, port) =>
        if (isIpv4) {
          new InetSocketAddress("127.0.0.1", port)
        } else {
          new InetSocketAddress("::1", port)
        }
    })

  implicit val arbNodeInfo: Arbitrary[NodeInfo] =
    Arbitrary(Gen.zip(
      arbitrary[Protocol],
      arbitrary[InetSocketAddress],
      arbitrary[PublicKey]
    ).map {
      case (protocol, address, nodeId) =>
        NodeInfo(
          protocol,
          address,
          nodeId
        )
    })

}

final class NodeInfoTest extends ModuleCompanionTest(NodeInfo) {

  override val arbT = NodeInfoTest.arbNodeInfo

  test("a serialised ipv4 node info is 39 bytes") {
    forAll { (protocol: Protocol, port: Port, publicKey: PublicKey) =>
      val nodeInfo = NodeInfo(protocol, new InetSocketAddress("127.0.0.1", port.value), publicKey)
      val byteCount =
        NodeInfo.toBytes(nodeInfo)
          .getOrElse(fail("Encoding failed"))
          .size
      assert(byteCount == 39)
    }
  }

  test("a serialised ipv6 node info is 51 bytes") {
    forAll { (protocol: Protocol, port: Port, publicKey: PublicKey) =>
      val nodeInfo = NodeInfo(protocol, new InetSocketAddress("::1", port.value), publicKey)
      val byteCount =
        NodeInfo.toBytes(nodeInfo)
          .getOrElse(fail("Encoding failed"))
          .size
      assert(byteCount == 51)
    }
  }

}
