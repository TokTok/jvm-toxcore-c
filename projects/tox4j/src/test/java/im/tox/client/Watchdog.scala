package im.tox.client

import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.atomic.AtomicLong
import java.util.{Timer, TimerTask}

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.concurrent.duration.Duration

/**
 * Starts a thread that periodically checks whether the application is still
 * running and throws an exception after a given timeout.
 *
 * @param name Name to appear in [[toString]] and for identifying watchdog timers.
 * @param period Interval between checks for updates. Should be significantly less than
 *               [[timeout]] for the watchdog to work as expected.
 * @param timeout Time after the last call to [[ping]] before which to call [[onExpired]].
 * @param memoryLimit Memory usage before logging warnings. Defaults to 2GB.
 * @param onExpired Function to call when this [[Watchdog]] expires.
 */
final case class Watchdog(
    name: String,
    period: Duration,
    timeout: Duration,
    memoryLimit: Long = Int.MaxValue
)(onExpired: Watchdog => Unit) {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  private val Disabled = Long.MaxValue
  private val Stopped = Long.MinValue

  /**
   * Start at the maximum time, so the first call to [[ping]] will
   * effectively start the watchdog.
   */
  private val lastUpdate = new AtomicLong(Disabled)

  private implicit val scheduler: ScheduledExecutorService = scalaz.stream.DefaultScheduler

  new Timer(toString, true).schedule(new TimerTask {
    override def run(): Unit = {
      while (lastUpdate.get != Stopped) {
        if (System.currentTimeMillis() - timeout.toMillis > lastUpdate.get) {
          logger.debug(s"${Watchdog.this} expired")
          lastUpdate.lazySet(Stopped)
          onExpired(Watchdog.this)
        }
        val allocatedMemory = Runtime.getRuntime.totalMemory
        if (allocatedMemory > memoryLimit) {
          logger.warn(s"Memory limit reached: $allocatedMemory > $memoryLimit")
        }
      }
      logger.debug(s"${Watchdog.this} stopped")
    }
  }, period.toMillis, period.toMillis)

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
