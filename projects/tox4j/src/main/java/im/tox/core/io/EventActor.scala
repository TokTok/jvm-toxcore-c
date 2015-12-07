package im.tox.core.io

import com.typesafe.scalalogging.Logger
import im.tox.core.dht.Dht
import im.tox.core.network.EventProcessor
import org.slf4j.LoggerFactory

import scalaz.concurrent.Task
import scalaz.stream._
import scalaz.{-\/, \/-}

@SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Any"))
object EventActor {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  def make(
    dht: Dht
  )(
    eventSource: Process[Task, IO.Event],
    actionSink: Sink[Task, IO.Action]
  ): Process[Task, Unit] = {
    for {
      action <- eventSource.stateScan(dht)(EventProcessor.processEvent)
      _ <- {
        val actionSource: Process0[IO.Action] =
          action match {
            case -\/(error) =>
              logger.debug("Error: " + error)
              Process.fail(error.exception)
            case \/-(actions) =>
              logger.debug("Actions: " + actions)
              Process.emitAll(actions).repeat.take(2)
          }
        actionSource.toSource.to(actionSink)
      }
    } yield {
      logger.debug("Processed event")
    }
  }

}
