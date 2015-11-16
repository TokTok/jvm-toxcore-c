package sbt.tox4j

import java.io.File

import com.trueaccord.scalapb.ScalaPbPlugin._
import sbt.Keys._
import sbt._
import sbt.tox4j.Jni.Keys._

// scalastyle:off
object ProtobufJni extends OptionalPlugin {

  val Protobuf = config("protoc")

  object Keys {
    val protoc = settingKey[File]("The path+name of the protoc executable.")
    val generate = taskKey[Seq[File]]("Compile the protobuf sources.")
  }

  import Keys._

  override val moduleSettings = inConfig(Protobuf)(Seq(
    sourceDirectory := (sourceDirectory in Compile).value / "protobuf",

    protoc := file("protoc"),

    generate <<= (
      streams,
      managedNativeSource in Compile,
      sourceDirectory,
      protoc
    ) map sourceGeneratorTask

  )) ++ protobufSettings ++ Seq(
    version in protobufConfig := "3.0.0-alpha-3",
    javaConversions in protobufConfig := false, // TODO(iphydf): Set this to true.
    flatPackage in protobufConfig := true,

    sourceGenerators in Compile <+= generate in Protobuf,
    jniSourceFiles in Compile ++= ((managedNativeSource in Compile).value ** "*.pb.cpp").get
  )

  private def sourceGeneratorTask(
    streams: TaskStreams,
    managedNativeSource: File,
    sourceDirectory: File,
    protoc: File
  ) = {
    val compile = { (in: Set[File]) =>
      in.foreach(schema => streams.log.debug(s"Compiling schema $schema"))

      // Compile to C++ sources.
      compileProtoc(protoc, in, managedNativeSource, sourceDirectory, streams.log)

      Set.empty[File]
    }

    val schemas = (sourceDirectory ** "*.proto").get.map(_.getAbsoluteFile)

    FileFunction.cached(
      streams.cacheDirectory / "protobuf",
      inStyle = FilesInfo.lastModified,
      outStyle = FilesInfo.exists
    )(compile)(schemas.toSet).toSeq
  }

  private def compileProtoc(
    protoc: File,
    schemas: Set[File],
    managedNativeSource: File,
    sourceDirectory: File,
    log: Logger
  ): Unit = {
    val cppOut = managedNativeSource
    cppOut.mkdirs()

    val protocOptions = Seq(s"--cpp_out=${cppOut.absolutePath}")

    log.debug("protoc options:")
    protocOptions.map("\t" + _).foreach(log.debug(_))

    val exitCode =
      try {
        val command = Seq(protoc.getPath) ++ Seq("-I" + sourceDirectory.absolutePath) ++ protocOptions ++ schemas.map(_.absolutePath)
        command ! log
      } catch {
        case e: Exception =>
          throw new RuntimeException(s"error occured while compiling protobuf files: ${e.getMessage}", e)
      }
    if (exitCode != 0) {
      sys.error(s"protoc returned exit code: $exitCode")
    }
  }

}
