package im.tox.sbt.lint

import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

object Xlint extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = JvmPlugin

  override val projectSettings = Seq(
    scalacOptions ++= Seq("-Xlint", "-unchecked", "-feature", "-deprecation"),
    javacOptions ++= Seq("-Xlint:deprecation", "-Xlint:unchecked")
  )

}
