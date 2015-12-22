package im.tox.sbt.lint

import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

object Hairyfotr extends AutoPlugin {

  override def trigger: PluginTrigger = noTrigger
  override def requires: Plugins = JvmPlugin

  // Enable foursquare linter.
  override val projectSettings = Seq(
    resolvers ++= Seq(
      "Linter Repository" at "https://hairyfotr.github.io/linteRepo/releases",
      Resolver.sonatypeRepo("snapshots")
    ),
    addCompilerPlugin("org.psywerx.hairyfotr" %% "linter" % "0.1-SNAPSHOT"),
    scalacOptions in Test += "-P:linter:disable:IdenticalStatements+VariableAssignedUnusedValue"
  )

}
