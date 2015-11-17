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
      IO.Action.StartTimer(delay, repeats) { duration => Some(IO.TimeEvent(duration)) }
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
            actionActor.kill ++ timeActor.kill ++ Process.halt
          } else {
            Process.empty[Task, Unit]
          }
        }

    val start = System.currentTimeMillis()
    actionActor.merge(timeActor).merge(testActor).run.run
    val end = System.currentTimeMillis()

    assert(end - start < (delay.toMillis * repeats * timers))
  }

}
