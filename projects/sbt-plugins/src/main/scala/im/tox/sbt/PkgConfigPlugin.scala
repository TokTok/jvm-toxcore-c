package im.tox.sbt

import im.tox.sbt.ConfigurePlugin.Configurations._
import im.tox.sbt.ConfigurePlugin.Keys._
import sbt.Keys._
import sbt._

object PkgConfigPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = ConfigurePlugin

  object autoImport {
    val pkgConfigPath = settingKey[String]("Search path for pkg-config.")
    val nativeLibraryDependencies = settingKey[Seq[ModuleID]]("Native library dependencies (pkg-config).")

    val pkgConfigCflags = taskKey[Seq[String]]("???")
    val pkgConfigLibs = taskKey[Seq[String]]("???")
  }

  import autoImport._

  def pkgConfig(
    log: Logger,
    nativeLibraryDependencies: Seq[ModuleID],
    pkgConfigPath: String,
    query: String
  ): Seq[String] = {
    val command = Process(
      Seq("pkg-config", s"--$query") ++ nativeLibraryDependencies.map(_.name), None,
      "PKG_CONFIG_PATH" -> pkgConfigPath
    )

    command lines log flatMap { line =>
      line.split(' ')
    }
  }

  val pkgconfigConfig = Seq(
    pkgConfigPath := sys.env.getOrElse("PKG_CONFIG_PATH", ""),

    pkgConfigCflags := pkgConfig(streams.value.log, nativeLibraryDependencies.value, pkgConfigPath.value, "cflags"),
    pkgConfigLibs := pkgConfig(streams.value.log, nativeLibraryDependencies.value, pkgConfigPath.value, "libs"),

    libCommonConfigFlags <++= pkgConfigCflags,
    libLdConfigFlags <++= pkgConfigLibs
  )

  override def projectSettings: Seq[Setting[_]] =
    pkgconfigConfig ++ Seq(
      nativeLibraryDependencies := Nil,
      nativeLibraryDependencies in NativeCompile <<= nativeLibraryDependencies,
      nativeLibraryDependencies in NativeTest <<= nativeLibraryDependencies
    )

}
