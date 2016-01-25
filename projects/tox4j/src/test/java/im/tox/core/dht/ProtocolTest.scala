package im.tox.core.dht

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import org.scalatest.FunSuite

object ProtocolTest {

  implicit val arbProtocol: Arbitrary[Protocol] =
    Arbitrary(arbitrary[Boolean].map { isUdp =>
      if (isUdp) {
        Protocol.Udp
      } else {
        Protocol.Tcp
      }
    })

}

final class ProtocolTest extends FunSuite
