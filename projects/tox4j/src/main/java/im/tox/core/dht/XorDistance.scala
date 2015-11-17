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
final case class XorDistance private (private val value: BigInt) extends AnyVal {

  def <(rhs: XorDistance): Boolean = value < rhs.value

  def <=(rhs: XorDistance): Boolean = value <= rhs.value

  def +(rhs: XorDistance): XorDistance = XorDistance(value + rhs.value)

  def mkString: String = {
    value.toByteArray.map(c => f"$c%02X").mkString
  }

  override def toString: String = {
    s"${getClass.getSimpleName}($mkString=$value)"
  }

}

object XorDistance {

  val Zero = XorDistance(0)

  private def toBigInt(bytes: Seq[Byte]): BigInt = {
    // Prepend zero-byte to avoid negative numbers.
    BigInt((0.toByte +: bytes).toArray)
  }

  private def signed(x: BigInt): BigInt = {
    x.toByteArray.toSeq match {
      case 0 +: tail if tail.nonEmpty => -BigInt(tail.toArray)
      case _                          => x
    }
  }

  private def apply(x: BigInt, y: BigInt): XorDistance = {
    assert(x >= 0)
    assert(y >= 0)
    XorDistance(signed(x ^ y).abs)
  }

  def apply(x: PublicKey, y: PublicKey): XorDistance = {
    XorDistance(toBigInt(x.value), toBigInt(y.value))
  }

}
