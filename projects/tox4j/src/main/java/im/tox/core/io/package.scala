package im.tox.core

import im.tox.core.dht.{Dht, NodeInfo}
import im.tox.core.io.IO.Event.TimedAction
import im.tox.core.network.PacketKind
import im.tox.core.network.packets.ToxPacket

import scala.concurrent.duration.{Duration, FiniteDuration}
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
    object Event {
      case object Shutdown extends Event
      final case class TimedAction(action: Dht => IO[Dht]) extends Event
      final case class Network(packet: Packet) extends Event
    }

    def apply[A](a: A): IO[A] = State.state(a)

    def sendTo(receiver: NodeInfo, packet: ToxPacket[PacketKind]): IO[Unit] = {
      addAction(Action.SendTo(receiver, packet))
    }

    def startTimer(id: TimerId, delay: FiniteDuration, repeat: Option[Int] = None)(event: Duration => Option[Event]): IO[Unit] = {
      addAction(Action.StartTimer(id, delay, repeat, event))
    }

    /**
     * Shortcut for [[startTimer]] with a [[TimedAction]].
     */
    def timedAction(id: TimerId, delay: FiniteDuration, repeat: Option[Int] = None)(action: (Duration, Dht) => IO[Dht]): IO[Unit] = {
      startTimer(id, delay, repeat) { duration =>
        Some(TimedAction { dht =>
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
