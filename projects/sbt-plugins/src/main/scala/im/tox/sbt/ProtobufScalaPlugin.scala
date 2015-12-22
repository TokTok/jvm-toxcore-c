package im.tox.sbt

import com.github.os72.protocjar.Protoc
import com.trueaccord.scalapb.ScalaPbPlugin._
import sbt.Keys._
import sbt._
import sbt.plugins.{IvyPlugin, JvmPlugin}

object ProtobufScalaPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = IvyPlugin && JvmPlugin

  override def projectSettings: Seq[Setting[_]] = protobufSettings ++ inConfig(protobufConfig)(Seq(
    runProtoc in protobufConfig := Def.task { (args: Seq[String]) =>
      Protoc.runProtoc(args.toArray)
    }.value,

    javaSource <<= (sourceManaged in Compile),
    scalaSource <<= (sourceManaged in Compile),

    version := "3.0.0-beta-1",
    javaConversions := true,
    flatPackage := true
  ))

}
