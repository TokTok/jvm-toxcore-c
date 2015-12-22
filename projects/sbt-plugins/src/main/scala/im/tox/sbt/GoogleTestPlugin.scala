package im.tox.sbt

import im.tox.sbt.ConfigurePlugin.Configurations._
import im.tox.sbt.NativeCompilePlugin.Keys._
import sbt.Keys._
import sbt._

object GoogleTestPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = NativeTestPlugin

  object Keys {
    val googleTestRepoUrl = settingKey[URL]("Location of gtest sources on the web.")
    val googleTestSource = settingKey[File]("Location of gtest sources on the file system.")
    val googleTestDownload = taskKey[Seq[File]]("Download gtest sources.")
  }

  import Keys._

  val gtestSettings = Seq(
    googleTestRepoUrl := url("https://github.com/google/googletest"),
    googleTestSource <<= sourceManaged { _ / "googletest" },

    managedSourceDirectories <+= googleTestSource { _ / "googletest" },
    managedSourceDirectories <+= googleTestSource { _ / "googletest/include" },

    googleTestDownload <<= Def.task {
      if (!crossCompiling.value && !googleTestSource.value.exists) {
        val log = streams.value.log

        val command = Seq("git", "clone", googleTestRepoUrl.value.toString, googleTestSource.value.getPath)
        log.debug(command.mkString(" "))
        command !! log
      }

      (googleTestSource.value / "googletest/src/gtest-all.cc").get
    },

    sourceGenerators <+= googleTestDownload
  )

  override def projectSettings: Seq[Setting[_]] = inConfig(NativeTest)(gtestSettings)

}
