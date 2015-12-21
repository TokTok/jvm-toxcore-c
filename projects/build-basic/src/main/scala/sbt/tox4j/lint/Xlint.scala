package sbt.tox4j.lint

import sbt.Keys._
import sbt.tox4j.OptionalPlugin

object Xlint extends OptionalPlugin {
  object Keys

  override val moduleSettings = Seq(
    scalacOptions ++= Seq("-Xlint", "-unchecked", "-feature", "-deprecation"),
    javacOptions ++= Seq("-Xlint:deprecation", "-Xlint:unchecked")
  )
}
