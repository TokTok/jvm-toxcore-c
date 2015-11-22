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

  test("triange inequality for corner cases") {
    val x = PublicKey.fromString("A6F238ABCF165A8210CA216D09D8C168739738E10A77DF7F3C67E0BAB9C500E6").get
    val y = PublicKey.fromString("A66C067F091AEDD155FE09F644BC9BBECE5192CE6F4598C379B7CC6D9903BB69").get
    val z = PublicKey.fromString("D002A6CD3E05DAEA573AE6FEA326F84FFF55FA5640E2F1C91C86681A22CD5777").get
    assert(XorDistance(x, z) <= XorDistance(x, y) + XorDistance(y, z))
  }

}
