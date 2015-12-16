package im.tox.client

import java.io.InputStreamReader
import java.net.URL
import java.util.concurrent.ScheduledExecutorService
import java.util.{Timer, TimerTask}

import com.google.common.io.CharStreams
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Try

/**
 * A class that periodically (1 minute by default) fetches a URL.
 *
 * If fetching fails, the old value is retained.
 */
final class Curl(url: URL, refreshEvery: Duration = 10 minutes) {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  private def update: Try[String] = {
    logger.debug(s"Updating $url")
    Try(CharStreams.toString(new InputStreamReader(url.openStream())).trim)
  }

  private var result = update

  private implicit val scheduler: ScheduledExecutorService = scalaz.stream.DefaultScheduler

  new Timer(s"Curl($url)", true).schedule(new TimerTask {
    override def run(): Unit = {
      result = update.orElse(result)
    }
  }, refreshEvery.toMillis, refreshEvery.toMillis)

  override def toString: String = result.toString

}
