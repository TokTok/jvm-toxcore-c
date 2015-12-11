package im.tox.core.io

import im.tox.core.dht.Dht
import im.tox.core.error.CoreError
import im.tox.core.io.IO.TimerId
import org.scalatest.FunSuite

import scala.concurrent.duration._
import scala.language.postfixOps
import scalaz._
import scalaz.concurrent.Task
import scalaz.stream.{Process, async}

final class EventActorTest extends FunSuite {

  private def processEvent1(event: IO.Event): State[Dht, CoreError \/ Seq[IO.Action]] = {
    assert(event == IO.Event.Shutdown)
    State.state(\/-(Seq(
      IO.Action.CancelTimer(TimerId("Timer")),
      IO.Action.CancelTimer(TimerId("Timer")),
      IO.Action.CancelTimer(TimerId("Timer")),
      IO.Action.CancelTimer(TimerId("Timer")),
      IO.Action.Shutdown
    )))
  }

  private def processEvent2(event: IO.Event): State[Dht, CoreError \/ Seq[IO.Action]] = {
    assert(event == IO.Event.Shutdown)
    State.state(\/-(Seq(
      IO.Action.StartTimer(TimerId("Timer1"), 1 second, Some(1), _ => None),
      IO.Action.StartTimer(TimerId("Timer2"), 1 second, Some(1), _ => None),
      IO.Action.StartTimer(TimerId("Timer3"), 1 second, Some(1), _ => None),
      IO.Action.StartTimer(TimerId("Timer4"), 1 second, Some(1), _ => None),
      IO.Action.Shutdown
    )))
  }

  test("decision making") {
    val actionQueue = async.boundedQueue[IO.Action](1)
    val eventQueue = async.boundedQueue[IO.Event](1)

    val eventActor = EventActor.make(Dht(), processEvent1)(eventQueue.dequeue, actionQueue.enqueue)

    val eventProducer = Process(IO.Event.Shutdown).toSource.to(eventQueue.enqueue)

    val actionConsumer = actionQueue.dequeue.flatMap { action =>
      if (action == IO.Action.Shutdown) {
        eventActor.kill
      } else {
        Process.empty[Task, Unit]
      }
    }

    eventActor
      .merge(eventProducer)
      .merge(actionConsumer)
      .run.run
  }

  test(s"linking $EventActor and $TimeActor together") {
    val actionQueue = async.boundedQueue[IO.Action](1)
    val eventQueue = async.boundedQueue[IO.Event](1)

    val eventActor = EventActor.make(Dht(), processEvent2)(eventQueue.dequeue, actionQueue.enqueue)
    val timeActor = TimeActor.make(actionQueue.dequeue, eventQueue.enqueue) ++ eventActor.kill

    val eventProducer = Process(IO.Event.Shutdown).toSource.to(eventQueue.enqueue)

    eventActor
      .merge(timeActor)
      .merge(eventProducer)
      .run.run
  }

}
