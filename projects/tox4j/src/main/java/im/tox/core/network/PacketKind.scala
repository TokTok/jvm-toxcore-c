package im.tox.core.network

import im.tox.core.ModuleCompanion
import scodec.codecs._
import scodec.{Attempt, Err}

sealed abstract class PacketKind(val id: Int)

/**
 * The list of all UDP packet ids: NET_PACKET_*. UDP packet ids are
 * the value of the first byte of each UDP packet and is how each packet gets
 * sorted to the right module that can handle it.
 */
// scalastyle:off magic.number
object PacketKind extends ModuleCompanion[PacketKind] {
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

  override val codec = uint8.exmap[PacketKind](
    {
      case PingRequest.id       => Attempt.successful(PingRequest)
      case PingResponse.id      => Attempt.successful(PingResponse)
      case NodesRequest.id      => Attempt.successful(NodesRequest)
      case NodesResponse.id     => Attempt.successful(NodesResponse)
      case CookieRequest.id     => Attempt.successful(CookieRequest)
      case CookieResponse.id    => Attempt.successful(CookieResponse)
      case CryptoHandshake.id   => Attempt.successful(CryptoHandshake)
      case CryptoData.id        => Attempt.successful(CryptoData)
      case DhtRequest.id        => Attempt.successful(DhtRequest)
      case LanDiscovery.id      => Attempt.successful(CryptoData)
      case OnionSend1.id        => Attempt.successful(OnionSend1)
      case OnionSend2.id        => Attempt.successful(OnionSend2)
      case OnionSend3.id        => Attempt.successful(OnionSend3)
      case AnnounceRequest.id   => Attempt.successful(AnnounceRequest)
      case AnnounceResponse.id  => Attempt.successful(AnnounceResponse)
      case OnionDataRequest.id  => Attempt.successful(OnionDataRequest)
      case OnionDataResponse.id => Attempt.successful(OnionDataResponse)
      case OnionReceive3.id     => Attempt.successful(OnionReceive3)
      case OnionReceive2.id     => Attempt.successful(OnionReceive2)
      case OnionReceive1.id     => Attempt.successful(OnionReceive1)

      case invalidId            => Attempt.failure(new Err.General("Invalid packed id: " + invalidId))
    },
    { self => Attempt.successful(self.id) }
  )

}
