package im.tox.client.http

import im.tox.tox4j.bench.TimingReport
import im.tox.tox4j.bench.ToxBenchBase._

final class TimeQueueTimingBench extends TimingReport {

  val size100k = range("size")(100000)
  val size1M = range("size")(1000000)

  def makeQueue(size: Int): TimeQueue = {
    val queue = new TimeQueue(size)
    (0 until size) foreach (_ => queue.update())
    queue
  }

  timing.of[TimeQueue] {

    measure method "averageRef" in {
      using(size100k.map(makeQueue)) in (_.averageRef)
    }

    measure method "averageOpt" in {
      using(size1M.map(makeQueue)) in (_.averageOpt)
    }

  }

}
