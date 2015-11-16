package im.tox.core.dht.handlers

import im.tox.core.dht.packets.dht.{PingPacket, PingRequestPacket, PingResponsePacket}
import im.tox.core.dht.{Dht, NodeInfo}
import im.tox.core.error.CoreError
import im.tox.core.io.IO

import scalaz.\/

object PingRequestHandler extends DhtPayloadHandler(PingRequestPacket) {

  /**
   * All ping requests received will be decrypted. If successfully decrypted a
   * response will be created then sent back to the same node.
   */
  override def apply(dht: Dht, sender: NodeInfo, packet: PingPacket): CoreError \/ IO[Dht] = {
    for {
      response <- makeResponse(
        dht.keyPair,
        sender.publicKey,
        PingResponsePacket,
        packet
      )
    } yield {
      for {
        _ <- IO.sendTo(sender, response)
      } yield {
        dht
      }
    }
  }

}
