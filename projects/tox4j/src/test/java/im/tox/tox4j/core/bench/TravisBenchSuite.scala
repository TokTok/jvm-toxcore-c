package im.tox.tox4j.core.bench

import im.tox.tox4j.bench.ToxBenchBase

final class TravisBenchSuite extends ToxBenchBase {

  include[IterateTimingBench]
  include[IterateMemoryBench]

}
