package im.tox.core.dht

import java.net.InetAddress

import im.tox.core.ModuleCompanion
import im.tox.core.typesafe.Security
import im.tox.tox4j.EnumerationMacros.sealedInstancesOf
import scodec.bits.ByteVector
import scodec.codecs._
import scodec.{Attempt, Err}

import scala.collection.immutable.TreeSet

sealed abstract class AddressFamily(addressLength: Int) {

  /**
   * [ip (in network byte order), length=4 bytes if ipv4, 16 bytes if ipv6]
   */
  val codec = {
    bytes(addressLength).xmap[InetAddress](
      bytes => InetAddress.getByAddress(bytes.toArray),
      address => ByteVector(address.getAddress)
    )
  }

}

case object AddressFamily extends ModuleCompanion[AddressFamily, Security.Sensitive] {

  /**
   * The reason for these numbers is because the numbers on my Linux machine
   * for ipv4 and ipv6 (the AF_INET and AF_INET6 defines) were 2 and 10.
   */
  private val Inet4Value = 2
  private val Inet6Value = 10

  private val Inet4AddressLength = 4
  private val Inet6AddressLength = 16

  case object Inet4 extends AddressFamily(Inet4AddressLength)
  case object Inet6 extends AddressFamily(Inet6AddressLength)

  implicit val ordAddressFamily: Ordering[AddressFamily] = Ordering.by {
    case Inet4 => Inet4Value
    case Inet6 => Inet6Value
  }

  lazy val values: TreeSet[AddressFamily] = sealedInstancesOf[AddressFamily]

  val codec = uint4.exmap[AddressFamily](
    {
      case Inet4Value => Attempt.successful(Inet4)
      case Inet6Value => Attempt.successful(Inet6)
      case invalid    => Attempt.failure(new Err.General("Invalid address family: " + invalid))
    }, {
      case Inet4 => Attempt.successful(Inet4Value)
      case Inet6 => Attempt.successful(Inet6Value)
    }
  )

}
