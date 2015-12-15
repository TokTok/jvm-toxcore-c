package im.tox.client

import java.util.concurrent.{ScheduledExecutorService, TimeoutException}
import java.util.concurrent.atomic.AtomicLong

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.concurrent.duration.Duration
import scalaz.{\/-, -\/}
import scalaz.stream.time

/**
 * Starts a thread that periodically checks whether the application is still
 * running and throws an exception after a given timeout.
 */
final case class Watchdog(name: String, period: Duration, timeout: Duration)(onExpired: Watchdog => Unit) {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  private val Disabled = Long.MaxValue
  private val Stopped = Long.MinValue

  /**
   * Start at the maximum time, so the first call to [[ping]] will
   * effectively start the watchdog.
   */
  private val lastUpdate = new AtomicLong(Disabled)

  private implicit val scheduler: ScheduledExecutorService = scalaz.stream.DefaultScheduler

  time.awakeEvery(period).takeWhile { _ =>
    lastUpdate.get != Stopped
  }.map { _ =>
    if (System.currentTimeMillis() - timeout.toMillis > lastUpdate.get) {
      throw new TimeoutException
    }
  }.run.runAsync {
    case -\/(failure) =>
      logger.debug(s"$this expired")
      onExpired(this)
    case \/-(()) =>
      logger.debug(s"$this stopped")
  }

  def ping(): Unit = {
    if (lastUpdate.get == Disabled) {
      logger.debug(s"Starting $this")
    }
    lastUpdate.lazySet(System.currentTimeMillis())
  }

  def stop(): Unit = {
    if (lastUpdate.get != Stopped) {
      logger.debug(s"Stopping $this")
      lastUpdate.lazySet(Stopped)
    }
  }

  override def toString: String = {
    s"Watchdog timer for $name, period = $period, timeout = $timeout"
  }

}
