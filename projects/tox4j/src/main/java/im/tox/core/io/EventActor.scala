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
    ioDht: IO[Dht],
    processEvent: IO.Event => State[Dht, CoreError \/ Seq[IO.Action]]
  )(
    eventSource: Process[Task, IO.Event],
    actionSink: Sink[Task, IO.Action]
  ): Process[Task, Unit] = {
    val (initialActions, dht) = ioDht.run(Nil)

    logger.debug(s"Running ${initialActions.size} initial actions")

    for {
      action <- Process.emit(\/-(initialActions)).merge(eventSource.stateScan(dht)(processEvent))
      () <- {
        val actionSource =
          action match {
            case -\/(CoreError.DecryptionError) =>
              logger.debug("Decryption error; ignoring packet")
              Process.empty
            case -\/(error) =>
              logger.debug(s"Error: $error")
              Process.fail(error.exception)
            case \/-(actions) =>
              logger.debug(s"Actions: $actions")
              Process.emitAll(actions)
          }
        actionSource.toSource.to(actionSink)
      }
    } yield {
      logger.trace("Processed event")
    }
  }

}
