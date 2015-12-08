package im.tox.core.io

import com.typesafe.scalalogging.Logger
import im.tox.core.dht.Dht
import im.tox.core.error.CoreError
import org.slf4j.LoggerFactory

import scalaz._
import scalaz.concurrent.Task
import scalaz.stream.{Sink, _}

@SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Any"))
case object EventActor {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  def make(
    dht: Dht,
    processEvent: IO.Event => State[Dht, CoreError \/ Seq[IO.Action]]
  )(
    eventSource: Process[Task, IO.Event],
    actionSink: Sink[Task, IO.Action]
  ): Process[Task, Unit] = {
    for {
      action <- eventSource.stateScan(dht)(processEvent)
      _ <- {
        val actionSource: Process0[IO.Action] =
          action match {
            case -\/(error) =>
              logger.debug("Error: " + error)
              Process.fail(error.exception)
            case \/-(actions) =>
              logger.debug("Actions: " + actions)
              Process.emitAll(actions)
          }
        actionSource.toSource.to(actionSink)
      }
    } yield {
      logger.debug("Processed event")
    }
  }

}
