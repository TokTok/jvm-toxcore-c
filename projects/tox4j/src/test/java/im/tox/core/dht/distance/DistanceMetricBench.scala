package im.tox.core.dht.distance

import im.tox.core.crypto.{PublicKey, PublicKeyTest}
import im.tox.tox4j.OptimisedIdOps._
import im.tox.tox4j.bench.ToxBenchBase._
import im.tox.tox4j.bench.{Confidence, TimingReport}
import org.scalacheck.Arbitrary

/**
 * Benchmark.
 */
abstract class DistanceMetricBench[Metric <: DistanceMetric[Metric]](metric: DistanceMetricCompanion[Metric]) extends TimingReport {

  protected override def confidence = Confidence.normal

  private def get[T](arb: Arbitrary[T]): T = {
    arb.arbitrary(org.scalacheck.Gen.Parameters.default).get
  }

  private def makePublicKey(x: Int): Seq[PublicKey] = {
    (0 to x).map(_ => get(PublicKeyTest.arbPublicKey))
  }

  private def makeDistances(keys: Seq[PublicKey]): Seq[Metric] = {
    for {
      key <- keys
      origin <- keys.headOption
    } yield {
      metric(origin, key)
    }
  }

  def makeDistancePairs(count: Int): Seq[(Metric, Metric)] = {
    count |> makePublicKey |> makeDistances |> (seq => seq.zip(seq))
  }

  val distPairs10k = iterations10k map makeDistancePairs
  val distPairs100k = iterations100k map makeDistancePairs

  timing of metric.getClass in {

    measure method "x < y" in {
      using(distPairs100k) in { dists =>
        for {
          dist <- dists
        } {
          dist._1 < dist._2
        }
      }
    }

    measure method "x.value < y.value" in {
      using(distPairs10k) in { dists =>
        for {
          dist <- dists
        } {
          dist._1.value < dist._2.value
        }
      }
    }

  }

}
