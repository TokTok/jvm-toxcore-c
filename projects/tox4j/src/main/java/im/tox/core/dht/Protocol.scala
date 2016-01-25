package im.tox.core.dht

sealed trait Protocol
object Protocol {
  case object Udp extends Protocol
  case object Tcp extends Protocol
}
