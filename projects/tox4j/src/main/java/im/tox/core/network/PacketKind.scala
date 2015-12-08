package im.tox.core.network

import im.tox.core.ModuleCompanion
import im.tox.core.typesafe.Security
import im.tox.tox4j.EnumerationMacros.sealedInstancesOf
import scodec.codecs._
import scodec.{Attempt, Err}

import scala.collection.immutable.TreeSet

sealed abstract class PacketKind(val id: Int)

/**
 * The list of all UDP packet ids: NET_PACKET_*. UDP packet ids are
 * the value of the first byte of each UDP packet and is how each packet gets
 * sorted to the right module that can handle it.
 */
// scalastyle:off magic.number
case object PacketKind extends ModuleCompanion[PacketKind, Security.NonSensitive] {

  /**
   * Ping(Request and response):
   *
   * [byte with value: 00 for request, 01 for response]
   */
  case object PingRequest extends PacketKind(0)
  case object PingResponse extends PacketKind(1)

  /**
   * Get nodes (Request):
   * Packet contents:
   * [byte with value: 02]
   */
  case object NodesRequest extends PacketKind(2)

  /**
   * Send_nodes (response):
   *
   * [byte with value: 04]
   */
  case object NodesResponse extends PacketKind(4)

  case object CookieRequest extends PacketKind(24)
  case object CookieResponse extends PacketKind(25)
  case object CryptoHandshake extends PacketKind(26)
  case object CryptoData extends PacketKind(27)

  /**
   * DHT Request packets:
   * [char with a value of 32]
   */
  case object DhtRequest extends PacketKind(32)

  case object LanDiscovery extends PacketKind(33)

  case object OnionSend1 extends PacketKind(128)
  case object OnionSend2 extends PacketKind(129)
  case object OnionSend3 extends PacketKind(130)

  case object AnnounceRequest extends PacketKind(131)
  case object AnnounceResponse extends PacketKind(132)
  case object OnionDataRequest extends PacketKind(133)
  case object OnionDataResponse extends PacketKind(134)

  case object OnionReceive3 extends PacketKind(140)
  case object OnionReceive2 extends PacketKind(141)
  case object OnionReceive1 extends PacketKind(142)

  implicit val ordPacketKind: Ordering[PacketKind] = Ordering.by(_.id)

  val values: TreeSet[PacketKind] = sealedInstancesOf[PacketKind]

  override val codec = uint8.exmap[PacketKind](
    { id => Attempt.fromOption(values.find(_.id == id), new Err.General("Invalid packed id: " + id)) },
    { self => Attempt.successful(self.id) }
  )

}
