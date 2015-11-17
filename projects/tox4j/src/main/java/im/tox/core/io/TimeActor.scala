package im.tox.core.io

import com.typesafe.scalalogging.Logger
import im.tox.core.io.IO.Action
import org.slf4j.LoggerFactory

import scalaz.concurrent.Task
import scalaz.stream._

@SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Any"))
object TimeActor {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  private implicit val scheduler = scalaz.stream.DefaultScheduler

  def make(
    actionSource: Process[Task, IO.Action],
    eventSink: Sink[Task, IO.Event]
  ): Process[Task, Unit] = {
    actionSource.take(1).flatMap { action =>
      make(actionSource, eventSink).merge(installTimer(eventSink, action))
    }
  }

  private def installTimer(eventSink: Sink[Task, IO.Event], action: IO.Action): Process[Task, Unit] = {
    action match {
      case startTimer @ Action.StartTimer(delay, repeat) =>
        val timer = time.awakeEvery(delay)
        repeat
          .fold(timer)(timer.take)
          .flatMap { duration =>
            startTimer.event(duration)
              .fold(Process.empty[Task, IO.Event])(Process.emit)
              .to(eventSink)
          }

      case _ => Process.empty
    }
  }

}
