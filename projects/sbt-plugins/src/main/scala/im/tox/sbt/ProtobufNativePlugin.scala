package im.tox.sbt

import com.trueaccord.scalapb.ScalaPbPlugin._
import im.tox.sbt.ConfigurePlugin.Configurations._
import im.tox.sbt.NativeCompilePlugin.Keys._
import org.apache.commons.io.FilenameUtils
import sbt.Keys._
import sbt._

object ProtobufNativePlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = ProtobufScalaPlugin && NativeCompilePlugin

  object Keys {
    val generateCpp = taskKey[Seq[File]]("Compile the protobuf sources to C++.")
  }

  import Keys._

  override def projectSettings: Seq[Setting[_]] = inConfig(protobufConfig)(Seq(
    generateCpp <<= (
      streams,
      runProtoc,
      sourceManaged in NativeCompile,
      sourceDirectory,
      crossPlatform in NativeCompile
    ) map sourceGeneratorTask,

    sourceGenerators in NativeCompile <+= generateCpp
  ))

  private def sourceGeneratorTask(
    streams: TaskStreams,
    runProtoc: Seq[String] => Int,
    managedNativeSource: File,
    sourceDirectory: File,
    crossPlatform: String
  ): Seq[File] = {
    val compile = { (in: Set[File]) =>
      in.foreach(schema => streams.log.debug(s"Compiling schema $schema"))

      // Compile to C++ sources.
      compileProtoc(runProtoc, in, managedNativeSource, sourceDirectory, streams.log)
    }

    val schemas = (sourceDirectory ** "*.proto").get.map(_.getAbsoluteFile)

    FileFunction.cached(
      streams.cacheDirectory / s"protobuf-$crossPlatform",
      inStyle = FilesInfo.lastModified,
      outStyle = FilesInfo.exists
    )(compile)(schemas.toSet).toSeq
  }

  private def compileProtoc(
    runProtoc: Seq[String] => Int,
    schemas: Set[File],
    managedNativeSource: File,
    sourceDirectory: File,
    log: Logger
  ): Set[File] = {
    managedNativeSource.mkdirs()

    val protocOptions = Seq(s"--cpp_out=${managedNativeSource.absolutePath}")

    log.debug("protoc options:")
    protocOptions.map("\t" + _).foreach(log.debug(_))

    val exitCode = runProtoc(Seq("-I" + sourceDirectory.absolutePath) ++ protocOptions ++ schemas.map(_.absolutePath))
    if (exitCode != 0) {
      sys.error(s"protoc returned exit code: $exitCode")
    }

    schemas.flatMap { schema =>
      val basename = FilenameUtils.removeExtension(schema.getName)
      Seq(basename + ".pb.cc", basename + ".pb.h")
    }.map(managedNativeSource / _)
  }

}
