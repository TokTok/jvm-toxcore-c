package im.tox.core.dht

import java.net.{Inet4Address, Inet6Address, InetSocketAddress}

import im.tox.core.ModuleCompanion
import im.tox.core.crypto.PublicKey
import im.tox.core.network.Port

/**
 * Packed node format:
 *
 * The packed node format is a way to store the node info in a small yet easy to
 * parse format. To store more than one node, simply append another one to the
 * previous one: [packed node 1][packed node 2]...
 *
 * The packed node format is used in many places in Tox. ip_type numbers 2 and
 * 10 are used to indicate an ipv4 or ipv6 UDP node. The number 130 is used for
 * an ipv4 TCP relay and 138 is used to indicate an ipv6 TCP relay. The TCP numbers
 * are just the UDP numbers + 128. The ip is 4 bytes for a ipv4 address (ip_type
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

  override val codec = {
    /**
     * [uint8_t ip_type (2 == IPv4, 10 == IPv6, 130 == TCP IPv4, 138 == TCP IPv6.
     *   First bit = protocol (0 is UDP, 1 is TCP),
     *   3 bits = nothing,
     *   4 bits = address family))
     * ]
     * [ip (in network byte order), length=4 bytes if ipv4, 16 bytes if ipv6]
     */
    val inetAddress = AddressFamily.codec.consume(_.codec) {
      case address: Inet4Address => AddressFamily.Inet4
      case address: Inet6Address => AddressFamily.Inet6
    }

    /**
     * [port (in network byte order), length=2 bytes]
     */
    val socketAddress = (inetAddress ~ Port.codec).xmap[InetSocketAddress](
      { case (inetAddress, port) => new InetSocketAddress(inetAddress, port.value) },
      { socketAddress => (socketAddress.getAddress, Port.unsafeFromInt(socketAddress.getPort)) }
    )

    /**
     * [char array (node_id), length=32 bytes]
     */
    (Protocol.codec ~ socketAddress ~ PublicKey.codec).xmap[NodeInfo](
      { case ((protocol, address), publicKey) => NodeInfo(protocol, address, publicKey) },
      { case NodeInfo(protocol, address, publicKey) => ((protocol, address), publicKey) }
    )
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
