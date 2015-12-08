package im.tox.core.dht.distance

import im.tox.core.crypto.PublicKey
import im.tox.core.crypto.PublicKeyTest._
import im.tox.tox4j.testing.GetDisjunction._
import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.PropertyChecks
import scodec.bits.ByteVector

abstract class DistanceMetricTest[Metric <: DistanceMetric[Metric]](
    metric: DistanceMetricCompanion[Metric]
) extends FunSuite with PropertyChecks {

  val Zero = {
    val zeroKey = PublicKey.fromBytes(ByteVector.fill(PublicKey.Size)(0)).get
    metric(zeroKey, zeroKey)
  }

  test("no network split at 0x7f/0x80") {
    val x = PublicKey.fromHexString("8000000000000000000000000000000000000000000000000000000000000000").get
    val y = PublicKey.fromHexString("7f00000000000000000000000000000000000000000000000000000000000000").get
    val z = PublicKey.fromHexString("0000000000000000000000000000000000000000000000000000000000000000").get

    val xz = metric(z, x)
    val yz = metric(z, y)
    assert((xz < yz) == (xz.value < yz.value))
  }

  test("less-than optimisation correctness") {
    forAll { (origin: PublicKey, x: PublicKey, y: PublicKey) =>
      val distance1 = metric(origin, x)
      val distance2 = metric(origin, y)
      assert((distance1 < distance2) == (distance1.value < distance2.value))
    }
  }

  test("a <= b is a < b || a == b") {
    forAll { (origin: PublicKey, x: PublicKey, y: PublicKey) =>
      val distance1 = metric(origin, x)
      val distance2 = metric(origin, y)
      assert((distance1 < distance2 || distance1 == distance2) == (distance1 <= distance2.value))
    }
  }

  test("coincidence") {
    forAll { (x: PublicKey) =>
      assert(metric(x, x) == Zero)
    }
  }

  test("non-negativity") {
    forAll { (x: PublicKey, y: PublicKey) =>
      whenever(x != y) {
        assert(metric(x, y) != Zero)
      }
    }
  }

  test("symmetric") {
    forAll { (x: PublicKey, y: PublicKey) =>
      assert(metric(x, y) == metric(y, x))
    }
  }

  test("triangle inequality") {
    forAll { (x: PublicKey, y: PublicKey, z: PublicKey) =>
      assert(metric(x, z) <= metric(x, y) + metric(y, z))
    }
  }

  test("triange inequality for negative numbers") {
    val x = PublicKey.fromHexString("0000000000000000000000000000000000000000000000000000000000000181").get
    val y = PublicKey.fromHexString("0000000000000000000000000000000000000000000000000000000000000100").get
    val z = PublicKey.fromHexString("0000000000000000000000000000000000000000000000000000000000000000").get

    assert(metric(x, z) <= metric(x, y) + metric(y, z))
  }

}
