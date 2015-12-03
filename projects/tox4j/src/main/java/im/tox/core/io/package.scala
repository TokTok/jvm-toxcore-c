package im.tox.core

import im.tox.core.dht.{Dht, NodeInfo}
import im.tox.core.network.PacketKind
import im.tox.core.network.packets.ToxPacket

import scala.concurrent.duration.{FiniteDuration, Duration}
import scalaz.State
import scalaz.stream.udp.Packet

package object io {

  private type S = Seq[IO.Action]
  type IO[A] = State[S, A]

  object IO {

    final case class TimerId(id: String) extends AnyVal
    final case class TimerIdFactory(tag: String) extends AnyVal {
      def apply(id: String): TimerId = TimerId(s"$tag.$id")
      def self: TimerId = TimerId(tag)
    }

    sealed trait Action
    object Action {
      case object Shutdown extends Action
      final case class SendTo(receiver: NodeInfo, packet: ToxPacket[PacketKind]) extends Action
      final case class StartTimer(id: TimerId, delay: FiniteDuration, repeat: Option[Int], event: Duration => Option[Event]) extends Action
      final case class CancelTimer(id: TimerId) extends Action
    }

    sealed trait Event
    case object ShutdownEvent extends Event
    final case class TimedActionEvent(action: Dht => IO[Dht]) extends Event
    final case class NetworkEvent(packet: Packet) extends Event

    def apply[A](a: A): IO[A] = State.state(a)

    def sendTo(receiver: NodeInfo, packet: ToxPacket[PacketKind]): IO[Unit] = {
      addAction(Action.SendTo(receiver, packet))
    }

    def startTimer(id: TimerId, delay: FiniteDuration, repeat: Option[Int] = None)(event: Duration => Option[Event]): IO[Unit] = {
      addAction(Action.StartTimer(id, delay, repeat, event))
    }

    /**
     * Shortcut for [[startTimer]] with a [[TimedActionEvent]].
     */
    def timedAction(id: TimerId, delay: FiniteDuration, repeat: Option[Int] = None)(action: (Duration, Dht) => IO[Dht]): IO[Unit] = {
      startTimer(id, delay, repeat) { duration =>
        Some(TimedActionEvent { dht =>
          action(duration, dht)
        })
      }
    }

    def cancelTimer(id: TimerId): IO[Unit] = {
      addAction(Action.CancelTimer(id))
    }

    private def addAction(action: Action): IO[Unit] = {
      State.modify(actions => action +: actions)
    }

  }

}
