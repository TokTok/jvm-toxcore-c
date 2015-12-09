package im.tox.core.dht.distance

import im.tox.core.crypto.PublicKey
import im.tox.core.dht.distance.XorDistance.{lessThan, signedXor, toBigInt}

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

  protected[distance] override def value: BigInt = signedXor(toBigInt(x.value), toBigInt(y.value))

  override def <(rhs: XorDistance): Boolean = { // scalastyle:ignore method.name
    val (origin, target1, target2) =
      if (rhs.x == x) {
        (x.value, y.value, rhs.y.value)
      } else {
        assert(rhs.y == y)
        (y.value, x.value, rhs.x.value)
      }

    assert(origin.length == target1.length)
    assert(origin.length == target2.length)

    // Signed xor for the first byte.
    val distance1 = Math.abs(origin.head ^ target1.head)
    val distance2 = Math.abs(origin.head ^ target2.head)

    if (distance1 < distance2) {
      true
    } else if (distance1 > distance2) {
      false
    } else {
      // Unsigned xor for the remaining bytes.
      lessThan(1, origin, target1, target2)
    }
  }

}

object XorDistance extends DistanceMetricCompanion[XorDistance] {

  private val Int256Max = BigInt(Byte.MaxValue +: Array.fill[Byte](PublicKey.Size - 1)(-1))

  private def toBigInt(bytes: Seq[Byte]): BigInt = {
    // Prepend zero-byte to avoid negative numbers.
    BigInt((0.toByte +: bytes).toArray)
  }

  /**
   * Interpret a [[BigInt]] as signed integer modulo the given number of bytes.
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

  private def unsigned(byte: Byte): Int = {
    byte & 0xff
  }

  @tailrec
  private def lessThan(index: Int, origin: Seq[Byte], target1: Seq[Byte], target2: Seq[Byte]): Boolean = {
    if (index == origin.length) {
      false
    } else {
      val distance1 = unsigned(origin(index)) ^ unsigned(target1(index))
      val distance2 = unsigned(origin(index)) ^ unsigned(target2(index))

      if (distance1 < distance2) {
        true
      } else if (distance1 > distance2) {
        false
      } else {
        lessThan(index + 1, origin, target1, target2)
      }
    }
  }

}
