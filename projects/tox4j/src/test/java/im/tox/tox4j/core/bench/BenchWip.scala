package im.tox.tox4j.core.bench

import im.tox.tox4j.bench.ToxBenchBase._
import im.tox.tox4j.bench.{Confidence, TimingReport}
import im.tox.tox4j.core.ToxCore

/**
 * Work in progress benchmarks.
 */
final class BenchWip extends TimingReport {

  protected override def confidence = Confidence.normal

  timing of classOf[ToxCore[Unit]] in {

    measure method "iterate+friends" in {
      using(iterations1k, toxWithFriends1k) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.iterate(()))
      }
    }

  }

  /**
   * Benchmarks we're not currently working on.
   */
  object HoldingPen {

    measure method "iterationInterval" in {
      usingTox(iterations1k) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.iterationInterval)
      }
    }

  }

}
