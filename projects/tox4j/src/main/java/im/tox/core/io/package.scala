package im.tox.core

import im.tox.core.dht.{Dht, NodeInfo}
import im.tox.core.error.CoreError
import im.tox.core.network.PacketKind
import im.tox.core.network.packets.ToxPacket

import scala.concurrent.duration.{Duration, FiniteDuration}
import scalaz._
import scalaz.stream.udp.Packet

package object io {

  private type S = List[IO.Action]
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
      final case class TimedAction(action: Dht => CoreError \/ IO[Dht]) extends Event
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
     * Shortcut for [[startTimer]] with an [[Event.TimedAction]].
     */
    def timedAction(
      id: TimerId,
      delay: FiniteDuration,
      repeat: Option[Int] = None
    )(
      action: (Duration, Dht) => CoreError \/ IO[Dht]
    ): IO[Unit] = {
      startTimer(id, delay, repeat) { duration =>
        Some(Event.TimedAction { dht =>
          action(duration, dht)
        })
      }
    }

    def cancelTimer(id: TimerId): IO[Unit] = {
      addAction(Action.CancelTimer(id))
    }

    private def addAction(action: IO.Action): IO[Unit] = {
      State.modify(actions => action +: actions)
    }

    abstract class Mergeable[A] {
      def empty: A
      def merge(a1: A, a2: A): A
    }
    implicit val UnitMergeable: Mergeable[Unit] = new Mergeable[Unit] {
      override def empty: Unit = ()
      override def merge(a1: Unit, a2: Unit): Unit = ()
    }

    def flatten[A](ts: List[IO[A]])(implicit merger: Mergeable[A]): IO[A] = {
      ts.fold(IO(merger.empty)) { (io1, io2) =>
        for {
          result1 <- io1
          actions1 <- State.get
          result2 <- io2
          actions2 <- State.get
          _ <- State.put(actions1 ++ actions2)
        } yield {
          merger.merge(result1, result2)
        }
      }
    }

  }

}
