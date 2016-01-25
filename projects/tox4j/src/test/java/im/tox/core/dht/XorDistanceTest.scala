package im.tox.core.dht

import im.tox.core.crypto.PublicKey
import im.tox.core.crypto.PublicKeyTest._
import org.scalatest.FunSuite
import org.scalatest.prop.PropertyChecks

final class XorDistanceTest extends FunSuite with PropertyChecks {

  private def toBigInt(x: Seq[Byte]): BigInt = {
    BigInt((0.toByte +: x).toArray)
  }

  test("coincidence") {
    forAll { (x: PublicKey) =>
      assert(XorDistance(x, x) == XorDistance.Zero)
    }
  }

  test("non-negativity") {
    forAll { (x: PublicKey, y: PublicKey) =>
      whenever(x != y) {
        assert(XorDistance(x, y) != XorDistance.Zero)
      }
    }
  }

  test("symmetric") {
    forAll { (x: PublicKey, y: PublicKey) =>
      assert(XorDistance(x, y) == XorDistance(y, x))
    }
  }

  test("triangle inequality") {
    forAll { (x: PublicKey, y: PublicKey, z: PublicKey) =>
      assert(XorDistance(x, z) <= XorDistance(x, y) + XorDistance(y, z))
    }
  }

}
