package im.tox.tox4j.core.bench

import im.tox.tox4j.bench.ToxBenchBase._
import im.tox.tox4j.bench.TimingReport
import im.tox.tox4j.core.ToxCore
import org.scalameter.KeyValue
import org.scalameter.api._

final class IterateTimingBench extends TimingReport {

  protected override def confidence = Seq[KeyValue](exec.benchRuns -> 100)

  timing of classOf[ToxCore[Unit]] in {

    measure method "iterate" in {
      usingTox(iterations10k) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.iterate(()))
      }
    }

    measure method "iterationInterval" in {
      usingTox(iterations100k) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.iterationInterval)
      }
    }

    measure method "iterate+friends" in {
      using(iterations1k, toxWithFriends1k) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.iterate(()))
      }
    }

  }

}
