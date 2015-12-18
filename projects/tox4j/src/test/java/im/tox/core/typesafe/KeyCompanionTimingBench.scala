package im.tox.core.typesafe

import im.tox.tox4j.bench.TimingReport
import im.tox.tox4j.bench.ToxBenchBase._
import org.scalacheck.{Arbitrary, Gen}

abstract class KeyCompanionTimingBench[T <: AnyVal, S <: Security] extends TimingReport {

  protected def companion: KeyCompanion[T, S]
  implicit protected def arbT: Arbitrary[T]

  def makeKeys(size: Int): Seq[T] = {
    Gen.listOfN(size, arbT.arbitrary)(Gen.Parameters.default).get
  }

  timing.of[KeyCompanion[T, S]] {

    measure method "toHexStringRef" in {
      using(iterations1k.map(makeKeys)) in (_.foreach(companion.toHexStringRef))
    }

    measure method "toHexString" in {
      using(iterations10k.map(makeKeys)) in (_.foreach(companion.toHexString))
    }

  }

}
