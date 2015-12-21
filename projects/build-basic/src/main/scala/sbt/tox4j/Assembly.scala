package sbt.tox4j

import java.io.File

import sbt.Keys._
import sbt._

object Assembly extends OptionalPlugin {

  object Keys {

    // settings
    val assemblyPath = settingKey[File]("Output directory for standalone binaries")
    val assemblyCache = settingKey[File]("Cache directory extracted classes")

    // tasks
    val assembly = taskKey[Unit]("Generates standalone binaries")

  }

  import Keys._

  override val moduleSettings = inConfig(Test)(Seq(

    // Put the linked library in here.
    assemblyPath := baseDirectory.value / "bin",
    assemblyCache := target.value / "assembly-cache",

    assembly := Def.task {
      val log = streams.value.log

      val extractedPath = assemblyCache.value / "extracted-classes"
      val stampPath = assemblyCache.value / "stamps"

      val options = javaOptions.value
      val classpath = fullClasspath.value
      val mainClasses = discoveredMainClasses.value

      val (outputs, jars) =
        classpath.foldLeft(0, Map[File, File]()) { (result, path) =>
          path.get(analysis) match {
            case Some(analysisInfo) =>
              (result._1 + analysisInfo.stamps.allProducts.size, result._2)
            case None =>
              val stamp = stampPath / path.data.getName
              if (!stamp.exists()) {
                (result._1, result._2 + ((path.data, stamp)))
              } else {
                result
              }
          }
        }

      log.info(s"Copying $outputs project outputs to assembly cache")
      if (jars.nonEmpty) {
        log.info(s"Extracting ${jars.size} dependency jars to assembly cache")
      }

      for (path <- classpath) {
        path.get(analysis) match {
          case Some(analysisInfo) =>
            IO.copyDirectory(path.data, extractedPath)
          case None =>
            for (stamp <- jars.get(path.data)) {
              IO.unzip(path.data, extractedPath, new NameFilter {
                override def accept(name: String): Boolean = {
                  Seq(
                    ".class",
                    ".properties"
                  ).exists(name.endsWith)
                }
              })
              IO.touch(stamp)
            }
        }
      }

      val mappings =
        for (path <- (extractedPath ** "*").get) yield {
          (path, extractedPath.toURI.relativize(path.toURI).getPath)
        }

      val outputJar = target.value / s"${name.value}.jar"

      log.info(s"Assembling ${mappings.size} files into standalone jar")
      IO.zip(mappings, outputJar)
    }.dependsOn(compile)
      .tag(Tags.Compile, Tags.CPU)
      .value

  ))
}

