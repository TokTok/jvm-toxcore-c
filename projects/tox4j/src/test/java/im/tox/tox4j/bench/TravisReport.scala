package im.tox.tox4j.bench

abstract class TravisReport extends ToxBenchBase {
  require(getClass.getSimpleName == "TravisBenchSuite")
}
