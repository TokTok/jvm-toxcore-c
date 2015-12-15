package im.tox.client

import com.typesafe.scalalogging.Logger
import im.tox.client.callbacks._
import im.tox.client.http.ToxClientHttpFrontend
import im.tox.tox4j.OptimisedIdOps._
import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.options.{SaveDataOptions, ToxOptions}
import im.tox.tox4j.impl.jni.{ToxAvImplFactory, ToxCoreImplFactory}
import im.tox.tox4j.testing.autotest.AutoTestSuite
import org.slf4j.LoggerFactory

import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.control.NonFatal

case object TestClient extends App {

  private val logger = Logger(LoggerFactory.getLogger(getClass))
  private val watchdog = Watchdog(name = this.toString, period = 1 second, timeout = 5 seconds) { watchdog =>
    logger.error("Watchdog timer expired; shutting down application")
    System.exit(1)
  }

  private def runTasks(tox: ToxCore[ToxClientState], av: ToxAv[ToxClientState])(state: ToxClientState): ToxClientState = {
    state.tasks.foldRight(state.copy(tasks = Nil)) { (task, state) =>
      try {
        task(tox, av, state)
      } catch {
        case NonFatal(exception) =>
          logger.error("Caught exception while executing task", exception)
          state
      }
    }
  }

  @tailrec
  private def mainLoop(httpServer: Option[ToxClientHttpFrontend], clients0: List[ToxClient]): Unit = {
    val (time, (clients, interval)) = AutoTestSuite.timed {
      watchdog.ping()
      httpServer.foreach(_.update(clients0))

      val clients =
        for (client <- clients0) yield {
          client.copy(
            state = client.state
            |> client.tox.iterate
            |> client.av.iterate
            |> runTasks(client.tox, client.av)
            |> ProfileManager.saveOnChange(client.tox, client.state.profile)
          )
        }

      val interval = (clients.map(_.av.iterationInterval) ++ clients.map(_.tox.iterationInterval)).min

      (clients, interval)
    }

    Thread.sleep((interval - time) max 0)

    mainLoop(httpServer, clients)
  }

  ToxClientOptions(args) { c =>
    val httpServer = c.httpPort.map(new ToxClientHttpFrontend(_))

    val predefined = c.load.map(key => ToxOptions(saveData = SaveDataOptions.SecretKey(key)))
    logger.info(s"Creating ${c.count} toxes (${predefined.length} with predefined keys)")
    val defaults = List.fill(c.count - predefined.length)(ToxOptions())
    logger.info(s"Additional default toxes: ${defaults.length}")

    ToxCoreImplFactory.withToxN[ToxClientState, Unit](predefined ++ defaults) { toxes =>
      (c.address, c.key) match {
        case (Some(address), Some(key)) =>
          logger.info(s"Bootstrapping all toxes to $address:${c.bootstrapPort.value}")
          toxes.foreach(_.bootstrap(address.getHostAddress, c.bootstrapPort, key))
        case _ =>
      }

      logger.info("Initialising AV sessions")
      ToxAvImplFactory.withToxAvN[ToxClientState, Unit](toxes) { avs =>
        logger.info("Initialising event listeners and client states")
        val clients =
          for (((tox, av), id) <- avs.zipWithIndex) yield {
            val handler = new ObservingEventListener(
              new MappingEventListener(
                new TestEventListener(id),
                new AudioVideoEventListener(id),
                new FriendListEventListener(id)
              ),
              new LoggingEventListener(id)
            )
            tox.callback(handler)
            av.callback(handler)

            val profile = ProfileManager.loadProfile(id, tox)
            // Save it again in case the file format changed.
            ProfileManager.saveProfile(tox, profile)
            ToxClient(tox, av, ToxClientState(
              tox.getAddress,
              tox.getDhtId,
              tox.getUdpPort,
              profile
            ))
          }

        logger.info("Starting event loop")
        mainLoop(httpServer, clients)
      }
    }
  }

}
