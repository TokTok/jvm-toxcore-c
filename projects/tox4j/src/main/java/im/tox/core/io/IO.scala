package im.tox.core.io

import im.tox.core.dht.NodeInfo
import im.tox.core.network.PacketKind
import im.tox.core.network.packets.ToxPacket

final case class IO[T](value: T, actions: Seq[IO.Action] = Nil) {

  def map[U](f: T => U): IO[U] = {
    copy(f(value))
  }

  def filter(f: T => Boolean): IO[T] = {
    this
  }

  def withFilter(f: T => Boolean): IO[T] = {
    this
  }

  def flatMap[U](f: T => IO[U]): IO[U] = {
    val mapped = f(value)
    mapped.copy(
      actions = mapped.actions ++ this.actions
    )
  }

  private def addAction(action: IO.Action): IO[T] = {
    copy(actions = action +: actions)
  }

}

object IO {

  sealed trait Action
  object Action {
    final case class SendTo(receiver: NodeInfo, packet: ToxPacket[PacketKind]) extends Action
  }

  val unit: IO[Unit] = IO(())

  def sendTo(receiver: NodeInfo, packet: ToxPacket[PacketKind]): IO[Unit] = {
    unit.addAction(Action.SendTo(receiver, packet))
  }

}
