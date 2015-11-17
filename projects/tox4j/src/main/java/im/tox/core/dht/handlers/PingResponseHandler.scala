package im.tox.core.dht.handlers

import im.tox.core.dht.packets.dht.{PingRequestPacket, NodesRequestPacket, PingPacket, PingResponsePacket}
import im.tox.core.dht.{Dht, NodeInfo}
import im.tox.core.error.CoreError
import im.tox.core.io.IO
import im.tox.core.network.PacketKind
import im.tox.core.network.packets.ToxPacket

import scalaz.\/

object PingResponseHandler extends DhtPayloadHandler(PingResponsePacket) {

  override def apply(dht: Dht, sender: NodeInfo, packet: PingPacket): CoreError \/ IO[Dht] = {
    for {
      pingRequest <- makeResponse(
        dht.keyPair,
        sender.publicKey,
        PingRequestPacket,
        PingPacket(0)
      )
    } yield {
      for {
        _ <- installPingTimer(dht, sender, pingRequest)
      } yield {
        dht.addNode(sender)
      }
    }
  }

  private def installPingTimer(
    dht: Dht,
    sender: NodeInfo,
    pingRequest: ToxPacket[PacketKind.PingRequest.type]
  ): IO[Unit] = {
    IO.startTimer(Dht.PingInterval, Some(1)) { _ =>
      Some(IO.TimedActionEvent { dht =>
        for {
          _ <- IO.sendTo(sender, pingRequest)
        } yield {
          dht
        }
      })
    }
  }

}
