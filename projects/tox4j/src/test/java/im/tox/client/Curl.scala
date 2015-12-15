package im.tox.client

import java.io.InputStreamReader
import java.net.URL
import java.util.concurrent.ScheduledExecutorService

import com.google.common.io.CharStreams

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Try
import scalaz.stream.time

/**
 * A class that periodically (1 minute by default) fetches a URL.
 *
 * If fetching fails, the old value is retained.
 */
final class Curl(url: URL, refreshEvery: Duration = 1 minute) {

  private def update: Try[String] = {
    Try(CharStreams.toString(new InputStreamReader(url.openStream())).trim)
  }

  private var result = update

  private implicit val scheduler: ScheduledExecutorService = scalaz.stream.DefaultScheduler

  time.awakeEvery(refreshEvery).map { _ =>
    result = update.orElse(result)
  }.run.run

  override def toString: String = result.toString

}
