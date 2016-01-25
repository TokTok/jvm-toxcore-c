package im.tox.core.network

import java.io.{ByteArrayInputStream, DataInputStream}
import java.net.InetSocketAddress

import com.typesafe.scalalogging.Logger
import im.tox.core.crypto.PlainText
import im.tox.core.dht.Dht
import im.tox.core.dht.handlers._
import im.tox.core.error.DecoderError
import im.tox.core.io.IO
import im.tox.core.network.handlers.ToxPacketHandler
import im.tox.core.network.packets.ToxPacket
import org.slf4j.LoggerFactory

import scalaz.{-\/, \/}

/**
 * The top-level protocol handler.
 */
final case class ToxHandler[T](handler: ToxPacketHandler[T]) extends ToxPacketHandler(ToxPacket) {

  override def apply(dht: Dht, origin: InetSocketAddress, packet: ToxPacket[PacketKind]): DecoderError \/ IO[Dht] = {
    val payload = new DataInputStream(new ByteArrayInputStream(packet.payload.data.toArray))
    for {
      packet <- handler.module.read(payload)
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

  override def apply(dht: Dht, origin: InetSocketAddress, packetData: PlainText): DecoderError \/ IO[Dht] = {
    logger.debug("Handling incoming packet: " + packetData)
    for {
      packet <- ToxPacket.read(new DataInputStream(new ByteArrayInputStream(packetData.data.toArray)))
      dht <- {
        handlers.get(packet.kind) match {
          case None =>
            -\/(DecoderError.Unimplemented(packet.kind.toString))
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
