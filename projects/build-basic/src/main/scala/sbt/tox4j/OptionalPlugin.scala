package sbt.tox4j

import sbt._

// scalastyle:off
abstract class OptionalPlugin extends AutoPlugin {
  override final def trigger: PluginTrigger = allRequirements

  def Keys: Any
  def moduleSettings: Seq[Setting[_]]
  override def projectSettings: Seq[Setting[_]] = moduleSettings
}
