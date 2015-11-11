package im.tox.core.network

import java.net.InetSocketAddress

import com.typesafe.scalalogging.Logger
import im.tox.core.crypto.PlainText
import im.tox.core.dht.Dht
import im.tox.core.dht.handlers._
import im.tox.core.error.CoreError
import im.tox.core.io.IO
import im.tox.core.network.handlers.ToxPacketHandler
import im.tox.core.network.packets.ToxPacket
import org.slf4j.LoggerFactory

import scalaz.{-\/, \/}

/**
 * The top-level protocol handler.
 */
final case class ToxHandler[T](handler: ToxPacketHandler[T]) extends ToxPacketHandler(ToxPacket) {

  override def apply(dht: Dht, origin: InetSocketAddress, packet: ToxPacket[PacketKind]): CoreError \/ IO[Dht] = {
    for {
      packet <- handler.module.fromBytes(packet.payload.data)
      dht <- handler(dht, origin, packet)
    } yield {
      dht
    }
  }

}

object ToxHandler extends ToxPacketHandler(PlainText) {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  val handlers = Map[PacketKind, ToxPacketHandler[ToxPacket[PacketKind]]](
    PacketKind.DhtRequest -> ToxHandler(DhtRequestHandler(ToxHandler)),
    PacketKind.PingRequest -> ToxHandler(DhtEncryptedHandler(PingRequestHandler)),
    PacketKind.PingResponse -> ToxHandler(DhtEncryptedHandler(PingResponseHandler)),
    PacketKind.NodesRequest -> ToxHandler(DhtEncryptedHandler(NodesRequestHandler)),
    PacketKind.NodesResponse -> ToxHandler(DhtEncryptedHandler(NodesResponseHandler))
  )

  override def apply(dht: Dht, origin: InetSocketAddress, packetData: PlainText): CoreError \/ IO[Dht] = {
    logger.debug("Handling incoming packet: " + packetData)
    for {
      packet <- ToxPacket.fromBytes(packetData.data)
      dht <- {
        handlers.get(packet.kind) match {
          case None =>
            -\/(CoreError.Unimplemented(packet.kind.toString))
          case Some(handler) =>
            logger.debug("Handler: " + handler)
            handler(dht, origin, packet)
        }
      }
    } yield {
      dht
    }
  }

}
