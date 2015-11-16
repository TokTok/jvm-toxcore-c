package im.tox.core

import im.tox.core.dht.NodeInfo
import im.tox.core.network.PacketKind
import im.tox.core.network.packets.ToxPacket

import scala.concurrent.duration.Duration
import scalaz.stream.udp.Packet
import scalaz.{StateFunctions, State}

package object io {

  type IO[A] = State[Seq[IO.Action], A]

  object IO extends StateFunctions {

    sealed trait Action
    object Action {
      case object Shutdown extends Action
      final case class SendTo(receiver: NodeInfo, packet: ToxPacket[PacketKind]) extends Action
      final case class StartTimer(delay: Duration, repeat: Int)(val event: Duration => Option[Event]) extends Action
    }

    sealed trait Event
    case object ShutdownEvent extends Event
    final case class TimeEvent(duration: Duration) extends Event
    final case class NetworkEvent(packet: Packet) extends Event

    def unit: IO[Unit] = State((_, ()))

    def sendTo(receiver: NodeInfo, packet: ToxPacket[PacketKind]): IO[Unit] = {
      modify(actions => Action.SendTo(receiver, packet) +: actions)
    }

    def apply[A](a: A): IO[A] = state(a)

  }

}
