package im.tox.core.dht.distance

import im.tox.core.crypto.PublicKey
import im.tox.core.dht.distance.XorDistance._

import scala.annotation.tailrec

/**
 * The distance between 2 peers can be defined as the XOR between the
 * 2 DHT public keys which are 32 byte numbers in big endian format. The smaller
 * this distance, the closer the peers are said to be.
 *
 * A DHT peer with public key 1 would be closer to one with public key 0 than
 * one with public key 5 for example because: 1 XOR 0 = 1 and 1 XOR 5 = 4. Since
 * 1 is smaller it means 1 is closer to 0 than to 5.
 */
final case class XorDistance(x: PublicKey, y: PublicKey) extends DistanceMetric[XorDistance] {

  protected[distance] override def value: BigInt = {
    signedXor(toBigInt(x.value), toBigInt(y.value))
  }

  override def <(rhs: XorDistance): Boolean = { // scalastyle:ignore method.name
    isLessThan(rhs)
  }

  def isLessThan(rhs: XorDistance): Boolean = {
    val (origin, target1, target2) =
      if (rhs.x == x) {
        (x.value, y.value, rhs.y.value)
      } else {
        assert(rhs.y == y)
        (y.value, x.value, rhs.x.value)
      }

    assert(origin.length == target1.length)
    assert(origin.length == target2.length)

    lessThan(origin, target1, target2)
  }

}

object XorDistance extends DistanceMetricCompanion[XorDistance] {

  /**
   * The maximum value of a 256 bit signed int. This value is 0x7fff...ff.
   */
  private val Int256Max = BigInt(Byte.MaxValue +: Array.fill[Byte](PublicKey.Size - 1)(-1))

  private def toBigInt(bytes: Seq[Byte]): BigInt = {
    // Prepend zero-byte to avoid negative numbers.
    BigInt((0.toByte +: bytes).toArray)
  }

  /**
   * Interpret a [[BigInt]] as signed integer modulo 256 bits.
   */
  private def signed(x: BigInt): BigInt = {
    if (x > Int256Max) {
      -BigInt(x.toByteArray.tail)
    } else {
      x
    }
  }

  private def signedXor(x: BigInt, y: BigInt): BigInt = {
    assert(x >= 0)
    assert(y >= 0)
    signed(x ^ y).abs
  }

  private def uint8(byte: Int): Int = {
    byte & 0xff
  }

  @tailrec
  private def lessThan(
    i: Int,
    signed1: Boolean,
    signed2: Boolean,
    origin: IndexedSeq[Byte],
    target1: IndexedSeq[Byte],
    target2: IndexedSeq[Byte]
  ): Boolean = {
    if (i == origin.length) {
      false
    } else {
      var distance1 = uint8(origin(i) ^ target1(i)) // scalastyle:ignore var.local
      var distance2 = uint8(origin(i) ^ target2(i)) // scalastyle:ignore var.local

      if (signed1) {
        distance1 = uint8(~distance1)
        if (i == origin.length - 1) {
          distance1 += 1
        }
      }

      if (signed2) {
        distance2 = uint8(~distance2)
        if (i == origin.length - 1) {
          distance2 += 1
        }
      }

      if (distance1 < distance2) {
        true
      } else if (distance1 > distance2) {
        false
      } else {
        lessThan(i + 1, signed1, signed2, origin, target1, target2)
      }
    }
  }

  private def isXorNegative(origin: Seq[Byte], target: Seq[Byte]): Boolean = {
    // TODO(iphydf): Scalac breaks this (compiles to false) with -optimise.
    // (origin.head < 0) != (target.head < 0)
    (origin.head < 0) ^ (target.head < 0)
  }

  private def lessThan(
    origin: IndexedSeq[Byte],
    target1: IndexedSeq[Byte],
    target2: IndexedSeq[Byte]
  ): Boolean = {
    val signed1 = isXorNegative(origin, target1)
    val signed2 = isXorNegative(origin, target2)

    lessThan(0, signed1, signed2, origin, target1, target2)
  }

}
