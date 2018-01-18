package im.tox.tox4j.core.bench

import im.tox.tox4j.bench.TravisReport

final class TravisBenchSuite extends TravisReport {

  include[IterateTimingBench]
  include[IterateMemoryBench]

}
