package im.tox.tox4j.core.bench

import im.tox.tox4j.bench.ToxBenchBase._
import im.tox.tox4j.bench.{Confidence, TimingReport}
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.callbacks.ToxCoreEventAdapter

/**
 * Work in progress benchmarks.
 */
final class BenchWip extends TimingReport {

  protected override def confidence = Confidence.normal

  val eventListener = new ToxCoreEventAdapter[Unit]

  timing of classOf[ToxCore] in {

    measure method "iterate+friends" in {
      using(iterations1k, toxWithFriends1k) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.iterate(eventListener)(()))
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
