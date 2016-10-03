package im.tox.tox4j.bench

@SuppressWarnings(Array(
  "org.wartremover.warts.Equals",
  "org.wartremover.warts.LeakingSealed"
))
abstract class TravisReport extends ToxBenchBase {
  require(getClass.getSimpleName == "TravisBenchSuite")
}
