package im.tox.client

import com.typesafe.scalalogging.Logger
import im.tox.client.callbacks._
import im.tox.client.http.ToxClientHttpFrontend
import im.tox.tox4j.OptimisedIdOps._
import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.data.{ToxNickname, ToxStatusMessage}
import im.tox.tox4j.core.options.{SaveDataOptions, ToxOptions}
import im.tox.tox4j.impl.jni.{ToxAvImplFactory, ToxCoreImplFactory}
import im.tox.tox4j.testing.GetDisjunction._
import im.tox.tox4j.testing.autotest.AutoTestSuite
import org.slf4j.LoggerFactory

import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random
import scala.util.control.NonFatal

case object TestClient extends App {

  private val logger = Logger(LoggerFactory.getLogger(getClass))
  private val watchdog = Watchdog(name = this.toString, period = 1 second, timeout = 5 seconds) { watchdog =>
    logger.error("Watchdog timer expired; shutting down application")
    System.exit(1)
  }

  // From ArmagetronAd's config/aiplayers.cfg.in
  private val names = Seq(
    "Outlook 3" -> "Anyone want to send me a postcard?",
    "Notepad 9" -> "Keeping track of important business.",
    "Word" -> "Writer's block :(",
    "Excel" -> "Spreadsheets everywhere!",
    "Emacs" -> "I'm better than Vi!",
    "Vi" -> "I'm better than Emacs!",
    "Pine" -> "Mutt. Nice mutt.",
    "Elm" -> "Professor, please. Have you seen Oak?",
    "LaTeX" -> "I'm the most beautiful thing you've ever seen.",
    "TeX" -> "You kids with your fancy macros all need Jesus.",
    "Gcc" -> "Internal compiler error; compilation completed with severe errors",
    "Gdb" -> "I've seen things you people wouldn't believe.",
    "MSVC++ 6" -> "I am something you people wouldn't believe",
    "Photoshop 2" -> "Let's just draw a happy little tree here",
    "Gimp" -> "Guile guile guile.",
    "Windows 7" -> "I'm a serious businessman.. oh look, a butterfly!",
    "Linux" -> "It's a unix system, I know this!",
    "Unreal 10" -> "Please don't kill me :/",
    "Quake" -> "Bring it on, noobs! *dies by rocket*"
  )

  private def runTasks(tox: ToxCore, av: ToxAv)(state: ToxClientState): ToxClientState = {
    if (state.tasks.isEmpty) {
      state
    } else {
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
  }

  @tailrec
  private def mainLoop(httpServer: ToxClientHttpFrontend, clients0: List[ToxClient]): Unit = {
    val (time, (clients, interval)) = AutoTestSuite.timed {
      watchdog.ping()
      httpServer.update(clients0)

      val clients =
        for (client <- clients0) yield {
          client.copy(
            state = client.state
            |> client.tox.iterate(client.handler)
            |> client.av.iterate(client.handler)
            |> runTasks(client.tox, client.av)
            |> ProfileManager.saveOnChange(client.tox, client.state.profile)
          )
        }

      val interval = (clients.map(_.av.iterationInterval) ++ clients.map(_.tox.iterationInterval)).min

      (clients, interval)
    }

    if (interval * 2 - time < 0) {
      logger.warn(s"Processing time ($time ms) is significantly longer than iteration interval ($interval ms)")
    }
    Thread.sleep((interval - time) max 0)

    mainLoop(httpServer, clients)
  }

  ToxClientOptions(args) { c =>
    val httpServer = new ToxClientHttpFrontend(c.httpPort)

    val predefined = c.load.map(key => ToxOptions(saveData = SaveDataOptions.SecretKey(key)))
    logger.info(s"Creating ${c.count} toxes (${predefined.length} with predefined keys)")
    val defaults = List.fill(c.count - predefined.length)(ToxOptions())
    logger.info(s"Additional default toxes: ${defaults.length}")

    ToxCoreImplFactory.withToxN[Unit](predefined ++ defaults) { toxes =>
      (c.address, c.key) match {
        case (Some(address), Some(key)) =>
          logger.info(s"Bootstrapping all toxes to $address:${c.bootstrapPort.value}")
          toxes.foreach(_.bootstrap(address.getHostAddress, c.bootstrapPort, key))
        case _ =>
      }

      logger.info("Initialising AV sessions")
      ToxAvImplFactory.withToxAvN[Unit](toxes) { avs =>
        logger.info("Initialising event listeners and client states")
        val clients = {
          val clientInfos = avs.zip(Stream.continually(Random.shuffle(names)).flatten).zipWithIndex

          for ((((tox, av), (name, statusMessage)), id) <- clientInfos) yield {
            val handler = new ObservingEventListener(
              new MappingEventListener(
                new TestEventListener(id),
                new AudioVideoEventListener(id),
                new FriendListEventListener(id)
              ),
              new LoggingEventListener(id)
            )

            // Update the profile with the new name.
            val profile = ProfileManager.loadProfile(id, tox)
              .withName(name)
              .withStatusMessage(statusMessage)

            // Set the name/status message in the tox instance.
            tox.setName(ToxNickname.fromString(name).get)
            tox.setStatusMessage(ToxStatusMessage.fromString(statusMessage).get)

            // Save it again, changing the name/status message and updating the file format.
            ProfileManager.saveProfile(tox, profile)
            ToxClient(tox, av, handler, ToxClientState(
              tox.getAddress,
              tox.getDhtId,
              tox.getUdpPort,
              profile
            ))
          }
        }

        logger.info("Starting event loop")
        mainLoop(httpServer, clients)
      }
    }
  }

}
