package im.tox.core.dht.distance

import im.tox.core.crypto.PublicKey
import im.tox.core.crypto.PublicKeyTest._
import im.tox.tox4j.testing.GetDisjunction._
import org.scalatest.FunSuite
import org.scalatest.prop.PropertyChecks
import scodec.bits.ByteVector

import scala.annotation.tailrec

abstract class DistanceMetricTest[Metric <: DistanceMetric[Metric]](
    final val metric: DistanceMetricCompanion[Metric]
) extends FunSuite with PropertyChecks {

  val ZeroKey = PublicKey.fromBytes(ByteVector.fill(PublicKey.Size)(0)).get

  val Zero = {
    metric(ZeroKey, ZeroKey)
  }

  @tailrec
  final def fillSuffix(prefix: String, suffix: String): String = {
    if (prefix.length >= PublicKey.Size * 2) {
      prefix
    } else {
      fillSuffix(prefix + suffix, suffix)
    }
  }

  @tailrec
  final def fillPrefix(prefix: String, suffix: String): String = {
    if (suffix.length >= PublicKey.Size * 2) {
      suffix
    } else {
      fillPrefix(prefix, prefix + suffix)
    }
  }

  test("signed distances full range last byte") {
    val prefixes = Seq(
      "FF",
      "00"
    )
    for {
      oPrefix <- prefixes
      yPrefix <- prefixes
      oLastByte <- 0 to 0xff
    } {
      val o = PublicKey.fromHexString(fillPrefix(oPrefix, f"$oLastByte%02X")).get
      val x = ZeroKey
      val y = PublicKey.fromHexString(fillPrefix(yPrefix, "00")).get

      val ox = metric(o, x)
      val oy = metric(o, y)
      assert((ox < oy) == (ox.value < oy.value))
    }
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

  test("triange inequality corner case") {
    val x = PublicKey.fromHexString(fillPrefix("00", "0181")).get
    val y = PublicKey.fromHexString(fillPrefix("00", "0100")).get
    val z = ZeroKey

    assert(metric(x, z) <= metric(x, y) + metric(y, z))
  }

}
