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
import shapeless.<:!<

import scala.util.Random

object AutoTestSuite {

  sealed abstract class Timed[A, R] {

    protected def wrap(time: Int, result: A): R

    def timed(block: => A): R = {
      val start = System.currentTimeMillis()
      val result = block
      val end = System.currentTimeMillis()
      wrap((end - start).toInt, result)
    }

  }

  implicit def otherTimed[A](implicit notUnit: A <:!< Unit): Timed[A, (Int, A)] = new Timed[A, (Int, A)] {
    protected def wrap(time: Int, result: A): (Int, A) = (time, result)
  }
  implicit val unitTimed: Timed[Unit, Int] = new Timed[Unit, Int] {
    protected def wrap(time: Int, result: Unit): Int = time
  }

  def timed[A, R](block: => A)(implicit timed: Timed[A, R]): R = timed.timed(block)

}

abstract class AutoTestSuite extends FunSuite with Timeouts {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  def maxParticipantCount: Int = 2

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
      val participantCount =
        if (maxParticipantCount == 2) {
          maxParticipantCount
        } else {
          new Random().nextInt(maxParticipantCount - 2) + 2
        }
      AutoTest(ToxCoreImplFactory, ToxAvImplFactory).run(participantCount, ToxOptions(), Handler)
    }
  }

}
