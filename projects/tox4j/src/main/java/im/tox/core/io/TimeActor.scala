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
    for {
      action <- actionSource
      () <- installTimer(eventSink, action)
    } yield {
      logger.debug("Performed time action")
    }
  }

  private def installTimer(eventSink: Sink[Task, IO.Event], action: IO.Action): Process[Task, Unit] = {
    action match {
      case startTimer @ Action.StartTimer(delay, repeat) =>
        for {
          duration <- time.awakeEvery(delay).take(repeat)
          () <- emitEvent(startTimer.event(duration), eventSink)
        } yield {
          logger.debug("Installed timer")
        }
      case _ =>
        Process.empty
    }
  }

  private def emitEvent(event: Option[IO.Event], eventSink: Sink[Task, IO.Event]): Process[Task, Unit] = {
    val eventSource = event.fold(Process.empty[Task, IO.Event])(Process.emit)
    eventSource.to(eventSink)
  }

}
