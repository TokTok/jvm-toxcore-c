package im.tox.core.network

import java.net.{Inet4Address, Inet6Address}

/**
 * It also contains datastructures used for ip addresses in toxcore. IP4 and IP6
 * are the datastructures for ipv4 and ipv6 addresses, IP is the datastructure
 * for storing either (the family can be set to AF_INET (ipv4) or AF_INET6
 * (ipv6). It can be set to another value like TCP_ONION_FAMILY, TCP_INET,
 * TCP_INET6 or TCP_FAMILY which are invalid values in the network modules but
 * valid values in some other module and denote a special type of ip).
 */
trait IpAddress
object IpAddress {
  final case class V4(aAddress: Inet4Address) extends IpAddress
  final case class V6(address: Inet6Address) extends IpAddress
  final case class TcpV4(address: Inet4Address) extends IpAddress
  final case class TcpV6(address: Inet6Address) extends IpAddress
  final case class Tcp() extends IpAddress
  final case class TcpOnion() extends IpAddress
}
