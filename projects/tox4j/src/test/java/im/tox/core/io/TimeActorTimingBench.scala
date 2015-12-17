package im.tox.core.io

import im.tox.core.io.IO.TimerId
import im.tox.tox4j.bench.ToxBenchBase._
import im.tox.tox4j.bench.{Confidence, TimingReport}
import org.apache.log4j.{Level, Logger}

import scala.concurrent.duration._
import scala.language.postfixOps
import scalaz.concurrent.Task
import scalaz.stream._

final class TimeActorTimingBench extends TimingReport {

  protected override def confidence = Confidence.normal

  val timerCounts = range("timers")(1000)

  timing.of[TimeActor.type] {

    performance of "replacing a single timer N times" in {
      def makeTask(actionCount: Int): Process[Task, Unit] = {
        Logger.getRootLogger.setLevel(Level.INFO)

        val actionQueue = async.boundedQueue[IO.Action](1)
        val eventQueue = async.boundedQueue[IO.Event](1)

        val timeActor = TimeActor.make(actionQueue.dequeue, eventQueue.enqueue)

        val timers = Process(
          IO.Action.StartTimer(TimerId("Shutdown"), 300 millis, None, _ => None)
        ).repeat.take(actionCount) ++ Process(IO.Action.Shutdown)

        val actionActor = timers.toSource.to(actionQueue.enqueue)

        timeActor.merge(actionActor)
      }

      using(timerCounts.map(makeTask)) in (_.run.run)
    }

    performance of "adding and cancelling a single timer N times" in {
      def makeTask(actionCount: Int): Process[Task, Unit] = {
        Logger.getRootLogger.setLevel(Level.INFO)

        val actionQueue = async.boundedQueue[IO.Action](1)
        val eventQueue = async.boundedQueue[IO.Event](1)

        val timeActor = TimeActor.make(actionQueue.dequeue, eventQueue.enqueue)

        val timers = Process(
          IO.Action.StartTimer(TimerId("Shutdown"), 300 millis, None, _ => None),
          IO.Action.CancelTimer(TimerId("Shutdown"))
        ).repeat.take(actionCount) ++ Process.emit(IO.Action.Shutdown)

        val actionActor = timers.toSource.to(actionQueue.enqueue)

        timeActor.merge(actionActor)
      }

      using(timerCounts.map(makeTask)) in (_.run.run)
    }

    performance of "adding N timers" in {
      def makeTask(actionCount: Int): Process[Task, Unit] = {
        Logger.getRootLogger.setLevel(Level.INFO)

        val actionQueue = async.boundedQueue[IO.Action](1)
        val eventQueue = async.boundedQueue[IO.Event](1)

        val timeActor = TimeActor.make(actionQueue.dequeue, eventQueue.enqueue)

        val timers = Process.range(0, actionCount).map { id =>
          IO.Action.StartTimer(TimerId(s"Shutdown.$id"), 300 millis, None, _ => None)
        } ++ Process(IO.Action.Shutdown)

        val actionActor = timers.toSource.to(actionQueue.enqueue)

        timeActor.merge(actionActor)
      }

      using(timerCounts.map(makeTask)) in (_.run.run)
    }

    performance of "adding N timers, then cancelling them all" in {
      def makeTask(actionCount: Int): Process[Task, Unit] = {
        Logger.getRootLogger.setLevel(Level.INFO)

        val actionQueue = async.boundedQueue[IO.Action](1)
        val eventQueue = async.boundedQueue[IO.Event](1)

        val timeActor = TimeActor.make(actionQueue.dequeue, eventQueue.enqueue)

        val range = Process.range(0, actionCount).toSource

        val timers = range.map { id =>
          IO.Action.StartTimer(TimerId(s"Shutdown.$id"), 300 millis, None, _ => None)
        } ++ range.map { id =>
          IO.Action.CancelTimer(TimerId(s"Shutdown.$id"))
        } ++ Process(IO.Action.Shutdown)

        val actionActor = timers.to(actionQueue.enqueue)

        timeActor.merge(actionActor)
      }

      using(timerCounts.map(makeTask)) in (_.run.run)
    }

  }

}
