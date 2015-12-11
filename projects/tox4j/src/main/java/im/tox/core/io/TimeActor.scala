package im.tox.core.io

import java.util.concurrent.ScheduledExecutorService

import com.typesafe.scalalogging.Logger
import im.tox.core.io.IO.{Action, Event, TimerId}
import org.slf4j.LoggerFactory

import scala.concurrent.duration.{Duration, FiniteDuration}
import scalaz.concurrent.Task
import scalaz.stream._
import scalaz.stream.async.mutable.Signal

@SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Any"))
case object TimeActor {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  /**
   * A wrapper for a mutable value and a signal containing the same value.
   *
   * This optimisation makes any adding/cancelling of timers 20 times faster,
   * because we avoid many calls to signal.get, which launches new [[Task]]s.
   * The reason we can safely do this optimisation is that we are sure that
   * the signal is only modified by this class ([[LocalSignal]]).
   *
   * We lose referential transparency, because [[get]] may now return
   * different values for the same object at different times.
   */
  private final class LocalSignal(
      private var value: Boolean = false, // scalastyle:ignore var.field
      private val signal: Signal[Boolean] = async.signalOf(false)
  ) {

    def set(value: Boolean): Task[Unit] = {
      signal.set(value).map { _ =>
        this.value = value
      }
    }

    def get: Boolean = {
      this.value
    }

    def discrete: Process[Task, Boolean] = {
      signal.discrete
    }

  }

  def make(
    actionSource: Process[Task, IO.Action],
    eventSink: Sink[Task, IO.Event]
  ): Process[Task, Unit] = {
    val actionQueue = async.boundedQueue[IO.Action](1)

    val producer = actionSource.to(actionQueue.enqueue)
    val consumer = processTimerActions(Map.empty, actionQueue.dequeue, eventSink)

    producer.wye(consumer)(wye.mergeHaltBoth)
  }

  /**
   * Process a single action at a time, so we can merge the timer resulting
   * from it with the recursive call to [[processTimerActions]]. This allows
   * multiple timers to run in parallel. If we do a simple flatMap over the
   * entire input stream, we have to wait for a timer to expire before the
   * next is started.
   */
  private def processTimerActions(
    timers: Map[TimerId, LocalSignal],
    actionSource: Process[Task, IO.Action],
    eventSink: Sink[Task, IO.Event]
  ): Process[Task, Unit] = {
    actionSource.once.flatMap {
      case IO.Action.Shutdown =>
        logger.debug(s"Shutting down $this")
        timers.values.foreach(_.set(true).run)
        Process.empty[Task, Unit]

      case action =>
        val (newTimers, startedTimer) = processTimerAction(removeOldTimers(timers), eventSink, action)

        // Listen for next action and start timer.
        processTimerActions(newTimers, actionSource, eventSink).merge(startedTimer)
    }
  }

  private def processTimerAction(
    timers: Map[TimerId, LocalSignal],
    eventSink: Sink[Task, Event],
    action: IO.Action
  ): (Map[TimerId, LocalSignal], Process[Task, Unit]) = {
    action match {
      case Action.StartTimer(id, delay, repeat, event) =>
        logger.debug(
          s"Starting timer $id with delay $delay " +
            repeat.fold("indefinitely")("and repeat " + _)
        )

        // New stop-timer signal.
        val stopTimer = new LocalSignal

        // Stop old timer.
        val newTimers = {
          timers.get(id).foreach(_.set(true).run)
          timers + (id -> stopTimer)
        }

        val timer = createTimer(eventSink, delay, repeat, event)
          .onComplete {
            // Set the stopTimer signal to true so it is removed when removeOldTimers is
            // called next time.
            Process.eval_(stopTimer.set(true))
          }

        val stoppableTimer = (stopTimer.discrete wye timer)(wye.interrupt)

        (newTimers, stoppableTimer)

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

  private def createTimer(
    eventSink: Sink[Task, Event],
    delay: FiniteDuration,
    repeat: Option[Int],
    event: (Duration) => Option[Event]
  ): Process[Task, Unit] = {
    implicit val scheduler: ScheduledExecutorService = scalaz.stream.DefaultScheduler

    repeat.toList
      .foldLeft(time.awakeEvery(delay))(_.take(_))
      .flatMap { duration =>
        event(duration)
          .fold(Process.empty[Task, Event])(Process.emit)
          .to(eventSink)
      }
  }

  /**
   * Remove timers that have been cancelled.
   */
  private def removeOldTimers(timers: Map[TimerId, LocalSignal]): Map[TimerId, LocalSignal] = {
    if (!timers.exists(_._2.get)) {
      timers
    } else {
      val result = timers.filterNot(_._2.get)
      val removedCount = timers.size - result.size
      if (removedCount != 0) {
        logger.debug(s"Cleaned up $removedCount old timers")
      }
      result
    }
  }

}
