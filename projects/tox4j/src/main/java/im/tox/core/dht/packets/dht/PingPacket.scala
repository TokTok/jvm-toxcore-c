package im.tox.core.dht.packets.dht

import java.io.{DataInputStream, DataOutput}

import im.tox.core.error.DecoderError
import im.tox.core.network.{PacketKind, PacketModuleCompanion}

import scalaz.{-\/, \/, \/-}

/**
 * Ping(Request and response):
 *
 * The main DHT packet types are ping requests and responses which are used to
 * check if another node is alive and get node packets which are used to query
 * another DHT node for the up to 4 nodes they know that are the closest to the
 * requested node .
 *
 * [byte with value: 00 for request, 01 for response]
 * [DHT public key of sender, length=32 bytes]
 * [random 24 byte nonce]
 * [Encrypted with the nonce, private DHT key of the sender and public DHT key of the receiver:
 *   [1 byte type (0 for request, 1 for response)]
 *   [8 byte (ping_id)]
 * ]
 *
 * The first byte of a ping request is a 0. This is then followed by the DHT
 * public key of the sender and a nonce. The encrypted part contains a byte
 * with the value 0 followed by a 8 byte ping ip which will be sent back in the
 * response. The ping id is used to make sure that the response received later
 * is a response for this ping and not a replayed response from a previous ping
 * which would have allowed an attacker to make the ping sender believe that
 * the node they are pinging is still up. The ping_id is also used so that a
 * node can't just send ping response packets to the node in order to make the
 * DHT module implementation reset its timeout, it makes sure the node has to
 * actually receive the request packet before sending a response.
 *
 * The first byte of a ping response is a 1. This is then followed by the DHT
 * public key of the one sending the response and a random nonce. The encrypted
 * part contains a byte with the value 1 followed by a 8 byte ping id that was
 * sent in the ping response.
 */
final case class PingPacket(
  pingId: Long
) extends AnyVal

abstract class PingPacketCompanion[Kind <: PacketKind](packetKind: Kind)
    extends PacketModuleCompanion[PingPacket, Kind](packetKind) {

  def isResponse: Boolean

  final override def write(self: PingPacket, packetData: DataOutput): Unit = {
    /**
     * [1 byte type (0 for request, 1 for response)]
     */
    packetData.write(
      if (isResponse) {
        1.toByte
      } else {
        0.toByte
      }
    )

    /**
     * [8 byte (ping_id)]
     */
    packetData.writeLong(self.pingId)
  }

  final override def read(packetData: DataInputStream): DecoderError \/ PingPacket = {
    for {
      /**
       * The reason for the 1 byte value in the encrypted part is because the key
       * used to encrypt both the request and response will be the same due to how
       * the encryption works it prevents a possible attacked from being able to
       * create a ping response without needing to decrypt the ping request.
       */
      _ <- packetData.readUnsignedByte() match {
        case 0 if isResponse  => -\/(DecoderError.InvalidFormat("Expected ping response but got request"))
        case 1 if !isResponse => -\/(DecoderError.InvalidFormat("Expected ping request but got response"))
        case 1 | 0            => \/-(())
        case invalid          => -\/(DecoderError.InvalidFormat("Invalid first byte of ping packet: " + invalid))
      }
    } yield {
      val pingId = packetData.readLong()
      PingPacket(pingId)
    }
  }

}
