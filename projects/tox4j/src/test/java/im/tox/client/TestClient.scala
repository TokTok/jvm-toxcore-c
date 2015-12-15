package im.tox.client

import java.io.{File, FileInputStream, FileOutputStream}
import java.net.InetSocketAddress

import com.sun.net.httpserver.HttpServer
import com.typesafe.scalalogging.Logger
import im.tox.client.callbacks._
import im.tox.client.proto.Profile
import im.tox.core.network.Port
import im.tox.tox4j.OptimisedIdOps._
import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.data.{ToxNickname, ToxPublicKey, ToxStatusMessage}
import im.tox.tox4j.core.options.{SaveDataOptions, ToxOptions}
import im.tox.tox4j.impl.jni.{ToxAvImplFactory, ToxCoreImpl, ToxCoreImplFactory}
import im.tox.tox4j.testing.GetDisjunction._
import im.tox.tox4j.testing.autotest.AutoTestSuite
import org.slf4j.LoggerFactory

import scala.annotation.tailrec
import scala.util.Try
import scala.util.control.NonFatal
import scalaz.concurrent.Future

final case class TestClient(
  tox: ToxCore[TestState],
  av: ToxAv[TestState],
  state: TestState
)

case object TestClient extends App {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  private val savePath = Seq(new File("tools/toxsaves"), new File("projects/tox4j/tools/toxsaves")).find(_.exists)

  def saveProfile(tox: ToxCore[TestState], profile: Profile): Unit = {
    savePath.foreach { savePath =>
      val output = new FileOutputStream(new File(savePath, tox.getPublicKey.toHexString))
      try {
        profile.writeTo(output)
        logger.info(s"Saved profile for ${tox.getPublicKey}")
      } finally {
        output.close()
      }
    }
  }

  def saveOnChange(tox: ToxCore[TestState], oldProfile: Profile)(state: TestState): TestState = {
    if (oldProfile != state.profile) {
      saveProfile(tox, state.profile)
    }
    state
  }

  def loadProfile(id: Int, tox: ToxCore[TestState]): Profile = {
    Try {
      val input = new FileInputStream(new File(savePath.get, tox.getPublicKey.toHexString))
      try {
        val profile = Profile.parseFrom(input)
        tox.setName(ToxNickname(profile.name.getBytes))
        tox.setStatusMessage(ToxStatusMessage(profile.statusMessage.getBytes))
        tox.setNospam(profile.nospam)
        tox.setStatus(ToxCoreImpl.convert(profile.status))
        logger.info(s"[$id] Adding ${profile.friendKeys.length} friends from saved friend list")
        profile.friendKeys.foreach(key => logger.debug(s"[$id] - $key"))
        profile.friendKeys.map(ToxPublicKey.fromHexString(_).get).foreach(tox.addFriendNorequest)
        logger.info(s"[$id] Successfully read profile for ${tox.getPublicKey}")
        profile
      } finally {
        input.close()
      }
    } getOrElse {
      val profile = Profile(
        name = tox.getName.toString,
        statusMessage = tox.getStatusMessage.toString,
        nospam = tox.getNospam,
        status = ToxCoreImpl.convert(tox.getStatus),
        friendKeys = tox.getFriendNumbers.map(tox.getFriendPublicKey).map(_.toHexString)
      )
      saveProfile(tox, profile)
      logger.info(s"[$id] Created new profile for ${tox.getPublicKey}")
      profile
    }
  }

  type Task[S] = (ToxCore[S], ToxAv[S], S) => S

  def runTasks(tox: ToxCore[TestState], av: ToxAv[TestState])(state: TestState): TestState = {
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
  def mainLoop(clients: List[TestClient]): Unit = {
    TestHttpServer.state = clients

    mainLoop {
      val (time, nextClients) = AutoTestSuite.timed {
        for (client <- clients) yield {
          client.copy(
            state = client.state
            |> client.tox.iterate
            |> client.av.iterate
            |> runTasks(client.tox, client.av)
            |> saveOnChange(client.tox, client.state.profile)
          )
        }
      }

      val interval = (clients.map(_.av.iterationInterval) ++ clients.map(_.tox.iterationInterval)).min
      Thread.sleep((interval - time) max 0)

      nextClients
    }
  }

  TestClientOptions(args) { c =>
    c.httpPort.foreach(TestHttpServer.start)

    val predefined = c.load.map(key => ToxOptions(saveData = SaveDataOptions.SecretKey(key)))
    logger.info(s"Creating ${c.count} toxes (${predefined.length} with predefined keys)")
    val defaults = List.fill(c.count - predefined.length)(ToxOptions())
    logger.info(s"Additional default toxes: ${defaults.length}")

    ToxCoreImplFactory.withToxN[TestState, Unit](predefined ++ defaults) { toxes =>
      (c.address, c.key) match {
        case (Some(address), Some(key)) =>
          logger.info(s"Bootstrapping all toxes to $address:${c.port.value}")
          toxes.foreach(_.bootstrap(address.getHostAddress, c.port, key))
        case _ =>
      }

      logger.info("Initialising AV sessions")
      ToxAvImplFactory.withToxAvN[TestState, Unit](toxes) { avs =>
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

            val profile = loadProfile(id, tox)
            // Save it again in case the file format changed.
            saveProfile(tox, profile)
            logger.info(s"[$id] Friend address: ${tox.getAddress.toHexString}")
            logger.info(s"[$id] DHT public key: ${tox.getDhtId.toHexString}")
            logger.info(s"[$id] UDP port: ${tox.getUdpPort}")
            TestClient(tox, av, TestState(profile))
          }

        logger.info("Starting event loop")
        mainLoop(clients)
      }
    }
  }

}
