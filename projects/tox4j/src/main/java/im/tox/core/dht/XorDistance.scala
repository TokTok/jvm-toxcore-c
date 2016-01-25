package im.tox.core.dht

import im.tox.core.crypto.PublicKey

/**
 * The distance between 2 peers can be defined as the XOR between the
 * 2 DHT public keys which are 32 byte numbers in big endian format. The smaller
 * this distance, the closer the peers are said to be.
 *
 * A DHT peer with public key 1 would be closer to one with public key 0 than
 * one with public key 5 for example because: 1 XOR 0 = 1 and 1 XOR 5 = 4. Since
 * 1 is smaller it means 1 is closer to 0 than to 5.
 */
// scalastyle:off method.name
final case class XorDistance private (private val data: BigInt) extends AnyVal {

  def <(rhs: XorDistance): Boolean = data < rhs.data

  def <=(rhs: XorDistance): Boolean = data <= rhs.data

  def +(rhs: XorDistance): XorDistance = XorDistance(data + rhs.data)

}

object XorDistance {

  val Zero = XorDistance(0)

  private def toBigInt(bytes: Seq[Byte]): BigInt = {
    // Prepend zero-byte to avoid negative numbers.
    BigInt((0.toByte +: bytes).toArray)
  }

  private def apply(x: BigInt, y: BigInt): XorDistance = {
    assert(x >= 0)
    assert(y >= 0)
    XorDistance(x ^ y)
  }

  def apply(x: PublicKey, y: PublicKey): XorDistance = {
    XorDistance(toBigInt(x.value), toBigInt(y.value))
  }

}
