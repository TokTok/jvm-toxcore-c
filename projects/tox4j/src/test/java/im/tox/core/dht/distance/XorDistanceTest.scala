package im.tox.core.dht.distance

import im.tox.core.crypto.PublicKey
import im.tox.tox4j.testing.GetDisjunction._

final class XorDistanceTest extends DistanceMetricTest(XorDistance) {

  test("no partitioning at 0x7f/0x80") {
    val x = PublicKey.fromHexString(fillSuffix("80", "00")).get
    val y = PublicKey.fromHexString(fillSuffix("7f", "00")).get
    val z = ZeroKey

    val xz = metric(z, x)
    val yz = metric(z, y)
    assert(yz < xz)
  }

  test("signed distances 128 == 128") {
    val o = PublicKey.fromHexString(fillPrefix("00", "80")).get
    val x = ZeroKey
    val y = PublicKey.fromHexString(fillPrefix("FF", "00")).get

    val ox = metric(o, x) // 128
    val oy = metric(o, y) // 128
    assert(ox == oy)
    assert(!(ox < oy))
    assert(!(oy < ox))
  }

  test("signed distances 129 > 127") {
    val o = PublicKey.fromHexString(fillPrefix("00", "81")).get
    val x = ZeroKey
    val y = PublicKey.fromHexString(fillPrefix("FF", "00")).get

    val ox = metric(o, x) // 129
    val oy = metric(o, y) // 127
    assert(ox != oy)
    assert(oy.value < ox.value)
    assert(oy < ox)
  }

  test("signed distances 127 < 129") {
    val o = PublicKey.fromHexString(fillPrefix("FF", "81")).get
    val x = ZeroKey
    val y = PublicKey.fromHexString(fillPrefix("FF", "00")).get

    val ox = metric(o, x) // 127
    val oy = metric(o, y) // 129
    assert(ox != oy)
    assert(ox.value < oy.value)
    assert(ox < oy)
  }

}
