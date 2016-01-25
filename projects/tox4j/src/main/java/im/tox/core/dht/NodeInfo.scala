package im.tox.core.dht

import java.io.{DataInputStream, DataOutput}
import java.net.{Inet4Address, Inet6Address, InetAddress, InetSocketAddress}

import im.tox.core.ModuleCompanion
import im.tox.core.crypto.PublicKey
import im.tox.core.error.DecoderError

import scalaz.{-\/, \/, \/-}

/**
 * Packed node format:
 *
 * The packed node format is a way to store the node info in a small yet easy to
 * parse format. To store more than one node, simply append another one to the
 * previous one: [packed node 1][packed node 2]...
 *
 * The packed node format is used in many places in Tox. ip_type numbers 2 and
 * 10 are used to indicate an ipv4 or ipv6 UDP node. The number 130 is used for
 * an ipv4 TCP relay and 138 is used to indicate an ipv6 TCP relay. The reason
 * for these numbers is because the numbers on my Linux machine for ipv4 and
 * ipv6 (the AF_INET and AF_INET6 defines) were 2 and 10. The TCP numbers are
 * just the UDP numbers + 128. The ip is 4 bytes for a ipv4 address (ip_type
 * numbers 2 and 130). The ip is 16 bytes for an ipv6 address (ip_type numbers
 * 10 and 138). This is followed by 32 byte the public key of the node.
 *
 * Only the UDP ip_types (ip_type 2 and ip_type 10) are used in the DHT module
 * when sending nodes with the packed node format. This is because the TCP
 * ip_types are used to send TCP relay information and the DHT is UDP only.
 */
final case class NodeInfo(
  protocol: Protocol,
  address: InetSocketAddress,
  publicKey: PublicKey
)

object NodeInfo extends ModuleCompanion[NodeInfo] {

  private val UdpBit = 0
  private val TcpBit = 1

  private val Inet4Family = 2
  private val Inet6Family = 10

  private val Inet4AddressLength = 4
  private val Inet6AddressLength = 16

  override def write(self: NodeInfo, packetData: DataOutput): Unit = {
    /**
     * [uint8_t ip_type (2 == IPv4, 10 == IPv6, 130 == TCP IPv4, 138 == TCP IPv6.
     *   First bit = protocol (0 is UDP, 1 is TCP),
     *   3 bits = nothing,
     *   4 bits = address family))
     * ]
     */
    val protocol = self.protocol match {
      case Protocol.Udp => UdpBit
      case Protocol.Tcp => TcpBit
    }
    val addressFamily =
      self.address.getAddress match {
        case _: Inet4Address => Inet4Family
        case _: Inet6Address => Inet6Family
      }
    packetData.writeByte(protocol << 7 | addressFamily)

    /**
     * [ip (in network byte order), length=4 bytes if ipv4, 16 bytes if ipv6]
     */
    self.address.getAddress match {
      case _: Inet4Address => assert(self.address.getAddress.getAddress.length == Inet4AddressLength)
      case _: Inet6Address => assert(self.address.getAddress.getAddress.length == Inet6AddressLength)
    }
    packetData.write(self.address.getAddress.getAddress)

    /**
     * [port (in network byte order), length=2 bytes]
     */
    packetData.writeShort(self.address.getPort)

    /**
     * [char array (node_id), length=32 bytes]
     */
    PublicKey.write(self.publicKey, packetData)
  }

  override def read(packetData: DataInputStream): DecoderError \/ NodeInfo = {
    val ipType = packetData.readUnsignedByte()

    val protocol =
      ipType >> 7 match {
        // This can only ever be 0 or 1, since it was an unsigned byte.
        case UdpBit => Protocol.Udp
        case TcpBit => Protocol.Tcp
      }

    for {
      address <- {
        for {
          ipByteCount <- ipType & 0xf match {
            case Inet4Family => \/-(Inet4AddressLength)
            case Inet6Family => \/-(Inet6AddressLength)
            case invalid     => -\/(DecoderError.InvalidFormat("Invalid address family: " + invalid))
          }
        } yield {
          val ipBytes = Array.ofDim[Byte](ipByteCount)
          packetData.read(ipBytes)
          val address = InetAddress.getByAddress(ipBytes)

          val port = packetData.readUnsignedShort()
          new InetSocketAddress(address, port)
        }
      }
      publicKey <- PublicKey.read(packetData)
    } yield {
      NodeInfo(
        protocol,
        address,
        publicKey
      )
    }
  }

  /**
   * Create an ordering for [[NodeInfo]] where smaller means closer to the given public key.
   * @param publicKey The key to compute the distance from.
   */
  def distanceOrdering(publicKey: PublicKey): Ordering[NodeInfo] = {
    Ordering.fromLessThan[NodeInfo] { (a, b) =>
      val aDist = XorDistance(publicKey, a.publicKey)
      val bDist = XorDistance(publicKey, b.publicKey)
      aDist < bDist
    }
  }

}
