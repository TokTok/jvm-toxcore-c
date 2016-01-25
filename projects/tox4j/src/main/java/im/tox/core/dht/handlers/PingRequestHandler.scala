package im.tox.core.dht.handlers

import im.tox.core.dht.packets.dht.{PingPacket, PingRequestPacket, PingResponsePacket}
import im.tox.core.dht.{Dht, NodeInfo}
import im.tox.core.error.DecoderError
import im.tox.core.io.IO

import scalaz.{\/, \/-}

object PingRequestHandler extends DhtPayloadHandler(PingRequestPacket) {

  /**
   * All ping requests received will be decrypted. If successfully decrypted a
   * response will be created then sent back to the same node.
   */
  override def apply(dht: Dht, sender: NodeInfo, packet: PingPacket): DecoderError \/ IO[Dht] = {
    val response = makeResponse(
      dht.keyPair,
      sender.publicKey,
      PingResponsePacket,
      packet
    )

    \/- {
      for {
        () <- IO.sendTo(sender, response)
      } yield {
        dht
      }
    }
  }

}
