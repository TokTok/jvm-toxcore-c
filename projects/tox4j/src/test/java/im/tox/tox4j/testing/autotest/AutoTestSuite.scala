package im.tox.tox4j.testing.autotest

import com.typesafe.scalalogging.Logger
import im.tox.tox4j.TestConstants
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.{ToxAvImplFactory, ToxCoreImplFactory}
import im.tox.tox4j.testing.autotest.AutoTest.ClientState
import org.scalatest.FunSuite
import org.scalatest.concurrent.Timeouts
import org.slf4j.LoggerFactory

object AutoTestSuite {

  def timed[A](block: => A): (Int, A) = {
    val start = System.currentTimeMillis()
    val result = block
    val end = System.currentTimeMillis()
    ((end - start).toInt, result)
  }

}

abstract class AutoTestSuite extends FunSuite with Timeouts {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  val ParticipantCount = 2

  type S

  abstract class EventListener(val initial: S) extends AutoTest.EventListener[S] {

    override def selfConnectionStatus(
      connectionStatus: ToxConnection
    )(state: State): State = {
      debug(state, s"Our connection: $connectionStatus")
      state
    }

    override def friendConnectionStatus(
      friendNumber: Int,
      connectionStatus: ToxConnection
    )(state: State): State = {
      debug(state, s"Friend ${state.id(friendNumber)}'s connection: $connectionStatus")
      state
    }

  }

  def Handler: EventListener // scalastyle:ignore method.name

  protected def debug(state: ClientState[S], message: String): Unit = {
    logger.debug(s"[${state.id}] $message")
  }

  test("UDP") {
    failAfter(TestConstants.Timeout) {
      AutoTest(ToxCoreImplFactory, ToxAvImplFactory).run(ParticipantCount, ToxOptions(), Handler)
    }
  }

}
