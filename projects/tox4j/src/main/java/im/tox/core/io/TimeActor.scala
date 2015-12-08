package im.tox.core.io

import com.typesafe.scalalogging.Logger
import im.tox.core.io.IO.{TimerId, Event, Action}
import org.slf4j.LoggerFactory

import scalaz.concurrent.Task
import scalaz.stream._
import scalaz.stream.async.mutable.Signal

@SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Any"))
case object TimeActor {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  private implicit val scheduler = scalaz.stream.DefaultScheduler

  def make(
    actionSource: Process[Task, IO.Action],
    eventSink: Sink[Task, IO.Event]
  ): Process[Task, Unit] = {
    processTimerActions(Map.empty, actionSource, eventSink)
  }

  /**
   * Process a single action at a time, so we can merge the timer resulting
   * from it with the recursive call to [[processTimerActions]].
   */
  private def processTimerActions(
    timers: Map[TimerId, Signal[Boolean]],
    actionSource: Process[Task, IO.Action],
    eventSink: Sink[Task, IO.Event]
  ): Process[Task, Unit] = {
    actionSource.take(1).flatMap {
      case IO.Action.Shutdown =>
        logger.debug(s"Shutting down $this")
        timers.values.foreach(_.set(true).run)
        Process.empty[Task, Unit]

      case action =>
        logger.debug("NEXT: " + action)
        new Enumeration() {}
        val (newTimers, startedTimer) = processTimerAction(removeOldTimers(timers), eventSink, action)

        // Listen for next action and start timer.
        processTimerActions(newTimers, actionSource, eventSink).merge(startedTimer)
    }
  }

  private def processTimerAction(
    timers: Map[TimerId, Signal[Boolean]],
    eventSink: Sink[Task, Event],
    action: IO.Action
  ): (Map[TimerId, Signal[Boolean]], Process[Task, Unit]) = {
    action match {
      case Action.StartTimer(id, delay, repeat, event) =>
        logger.debug(s"Starting timer $id with delay $delay and repeat $repeat")

        // Stop old timer.
        timers.get(id).foreach(_.set(true).run)

        val stopTimer = async.signalOf(false)

        val timer =
          repeat
            .foldLeft(time.awakeEvery(delay))(_.take(_))
            .flatMap { duration =>
              event(duration)
                .fold(Process.empty[Task, Event])(Process.emit)
                .to(eventSink)
            }
            .onComplete {
              // Set the stopTimer signal to true so it is removed when removeOldTimers is
              // called next time.
              Process.eval_(stopTimer.set(true))
            }

        val stoppableTimer = (stopTimer.discrete wye timer)(wye.interrupt)

        (timers + (id -> stopTimer), stoppableTimer)

      case Action.CancelTimer(id) =>
        logger.debug(s"Cancelling timer $id")
        // Stop old timer.
        timers.get(id).foreach(_.set(true).run)
        (timers - id, Process.empty)

      case _ =>
        // No relevant actions.
        (timers, Process.empty)
    }
  }

  /**
   * Remove timers that have been cancelled.
   */
  private def removeOldTimers(timers: Map[TimerId, Signal[Boolean]]): Map[TimerId, Signal[Boolean]] = {
    val result = timers.filterNot(_._2.get.run)
    val removedCount = timers.size - result.size
    if (removedCount != 0) {
      logger.debug(s"Cleaned up $removedCount old timers")
    }
    result
  }

}
