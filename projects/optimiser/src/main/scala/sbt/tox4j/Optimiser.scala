package sbt.tox4j

import im.tox.optimiser.ByteCodeOptimiser
import sbt._
import sbt.Keys._

object Optimiser extends AutoPlugin {

  override val trigger = allRequirements

  override val projectSettings = Seq(
    manipulateBytecode in Compile := {
      val previous = (manipulateBytecode in Compile).value
      ByteCodeOptimiser.process(
        classpath = (dependencyClasspath in Compile).value.files,
        inputPath = target.value,
        outputPath = target.value
      )
      previous
    }
  )

}
