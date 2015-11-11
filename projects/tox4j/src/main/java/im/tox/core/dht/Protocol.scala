package im.tox.core.dht

import scodec.codecs._

sealed trait Protocol
object Protocol {

  case object Udp extends Protocol
  case object Tcp extends Protocol

  /**
   * First bit = protocol (0 is UDP, 1 is TCP),
   * 3 bits = nothing,
   */
  val codec = fixedSizeBits(uint4.sizeBound.lowerBound, bool(1)).xmap[Protocol](
    {
      case false => Udp
      case true  => Tcp
    }, {
      case Udp => false
      case Tcp => true
    }
  )

}
