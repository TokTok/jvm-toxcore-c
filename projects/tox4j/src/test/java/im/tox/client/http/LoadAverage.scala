package im.tox.client.http

import java.io.PrintWriter

/**
 * Shows the average time between iterations for the past 1 second, 1 minute, and 5 minutes.
 */
final class LoadAverage(iterationsPerSecond: Int) {

  private val queues = Seq(
    new TimeQueue(iterationsPerSecond),
    new TimeQueue(iterationsPerSecond * 60),
    new TimeQueue(iterationsPerSecond * 60 * 5)
  )

  def update(): Unit = {
    queues.foreach(_.update())
  }

  def print(out: PrintWriter): Unit = {
    out.println("Average time between iterations:")
    for (queue <- queues) {
      out.println(s"  last ${queue.maxSize}: ${queue.average}")
    }
  }

}
