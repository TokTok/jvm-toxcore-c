package im.tox.core.io

import com.typesafe.scalalogging.Logger
import org.scalatest.FunSuite
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.language.postfixOps
import scalaz.concurrent.Task
import scalaz.stream.{Process, async}

final class TimeActorTest extends FunSuite {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  test("parallel timers") {
    val delay = 100 milliseconds
    val repeats = 1
    val timers = 4

    val actionQueue = async.boundedQueue[IO.Action](10)
    val eventQueue = async.boundedQueue[IO.Event](10)

    val actionActor = Process(
      IO.Action.StartTimer(delay, Some(repeats))(_ => Some(IO.ShutdownEvent))
    ).repeat.take(timers).toSource.to(actionQueue.enqueue)

    val timeActor = TimeActor.make(actionQueue.dequeue, eventQueue.enqueue)

    val testActor =
      eventQueue.dequeue
        .scan(0) { (count, event) =>
          logger.debug("Event: " + event)
          count + 1
        }
        .flatMap { count =>
          if (count == repeats * timers) {
            actionActor.kill
          } else {
            Process.empty[Task, Unit]
          }
        }

    val start = System.currentTimeMillis()
    actionActor
      .merge(timeActor)
      .merge(testActor)
      .run.run
    val end = System.currentTimeMillis()

    assert(end - start < (delay.toMillis * repeats * timers))
  }

  test("conditionally expiring timers") {
    val delay = 100 milliseconds
    val repeats = 4

    val actionQueue = async.boundedQueue[IO.Action](10)
    val eventQueue = async.boundedQueue[IO.Event](10)

    val actionActor = Process(
      IO.Action.StartTimer(delay, Some(repeats))(_ => Some(IO.ShutdownEvent))
    ).toSource.to(actionQueue.enqueue)

    val timeActor = TimeActor.make(actionQueue.dequeue, eventQueue.enqueue)

    val testActor =
      eventQueue.dequeue
        .scan(0) { (count, event) =>
          logger.debug(s"Event $count: $event")
          count + 1
        }
        .flatMap { count =>
          if (count == repeats) {
            actionActor.kill
          } else {
            Process.empty[Task, Unit]
          }
        }

    actionActor
      .merge(timeActor)
      .merge(testActor)
      .run.run
  }

}
