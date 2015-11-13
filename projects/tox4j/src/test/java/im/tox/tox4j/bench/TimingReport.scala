package im.tox.tox4j.bench

/**
 * Base class for timing benchmarks. Use the `timing` method at the top level and any other DSL statements inside. Do
 * not use `performance of` at the top level, as it will not use [[ToxBenchBase.defaultConfig]].
 */
abstract class TimingReport extends ToxBenchBase {

  object timing extends Serializable { // scalastyle:ignore object.name
    def of(modulename: String): Scope = performance of modulename config defaultConfig
    def of[T](clazz: Class[T]): Scope = of(clazz.getSimpleName)
  }

}
