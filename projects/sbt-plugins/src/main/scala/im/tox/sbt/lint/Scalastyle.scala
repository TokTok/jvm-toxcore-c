package im.tox.sbt.lint

import org.scalastyle.sbt.ScalastylePlugin.scalastyleConfigUrl
import sbt._
import sbt.plugins.JvmPlugin

object Scalastyle extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = JvmPlugin

  def config(suffix: String): Some[URL] = Some(getClass.getResource(s"scalastyle$suffix-config.xml"))

  override val projectSettings = Seq(
    scalastyleConfigUrl := config(""),
    scalastyleConfigUrl in Test := config("-test")
  )

}
