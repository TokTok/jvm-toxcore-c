package im.tox.sbt

import java.io.File

import im.tox.sbt.ConfigurePlugin.Configurations._
import sbt.Keys._
import sbt._

object JavahPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = NativeCompilePlugin

  object Keys {
    val nativeClasses = taskKey[Map[String, Seq[String]]]("Hehe")
    val javah = taskKey[Seq[File]]("Hehe")
  }

  import Keys._

  val JavahNoUpdate = "\\[(?:No need to update|Creating|Overwriting) file RegularFileObject\\[(.+)\\]\\]".r

  val settings = Seq(
    nativeClasses := {
      val classes = (compile in Compile).value
        .relations
        .allProducts
        .filter(_.name.endsWith(".class"))
        .toSet
      NativeClassFinder(streams.value.log, classes)
    },

    javah <<= Def.task {
      val log = streams.value.log

      val classpath = (
        (classDirectory in Compile).value +: (dependencyClasspath in Compile).value.files
      ).mkString(File.pathSeparator)

      val classNames = nativeClasses.value.keys.toSeq

      val command = Seq(
        "javah", "-v",
        "-d", sourceManaged.value.getPath,
        "-classpath", classpath
      ) ++ classNames

      if (classNames.isEmpty) {
        log.info("No classes with native methods found; not running javah")
        Nil
      } else {
        log.info(s"Running javah to generate ${classNames.size} JNI headers")
        command lines log map {
          case JavahNoUpdate(header) => file(header)
        }
      }
    },

    sourceGenerators <+= javah
  )

  override def projectSettings: Seq[Setting[_]] = inConfig(NativeCompile)(settings)

}
