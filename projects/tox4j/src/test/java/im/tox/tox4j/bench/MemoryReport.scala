package im.tox.tox4j.bench

import org.scalameter.api._

import scala.reflect.ClassTag

/**
 * Base class for memory benchmarks. Use the `memory` method at the top level
 * and any other DSL statements inside. Do not use `performance of` at the
 * top level, as it will not use [[ToxBenchBase.defaultConfig]].
 */
abstract class MemoryReport extends ToxBenchBase {

  require(getClass.getSimpleName.endsWith("MemoryBench"))

  override def measurer: Measurer[Double] = new Executor.Measurer.MemoryFootprint

  object memory { // scalastyle:ignore object.name
    def of(modulename: String): Scope = {
      performance of (modulename + " (memory)") config defaultConfig
    }
    def of[T](block: => Unit)(implicit classTag: ClassTag[T]): Unit = {
      of(classTag.runtimeClass.getSimpleName).in(block)
    }
  }

}
