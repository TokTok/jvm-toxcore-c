package sbt.tox4j

import java.io.File

import com.github.os72.protocjar.Protoc
import com.trueaccord.scalapb.ScalaPbPlugin._
import sbt.Keys._
import sbt._
import sbt.tox4j.Jni.Keys._

// scalastyle:off
object ProtobufJni extends OptionalPlugin {

  val Protobuf = config("protoc")

  object Keys {
    val generate = taskKey[Seq[File]]("Compile the protobuf sources.")
  }

  import Keys._

  override val moduleSettings = inConfig(Protobuf)(Seq(
    sourceDirectory := (sourceDirectory in Compile).value / "protobuf",

    generate <<= (
      streams,
      runProtoc in protobufConfig,
      managedNativeSource in Compile,
      sourceDirectory
    ) map sourceGeneratorTask

  )) ++ protobufSettings ++ Seq(
    runProtoc in protobufConfig := Def.task { (args: Seq[String]) =>
      Protoc.runProtoc(args.toArray)
    }.value,

    version in protobufConfig := "3.0.0-beta-1",
    javaConversions in protobufConfig := false, // TODO(iphydf): Set this to true.
    flatPackage in protobufConfig := true,

    sourceGenerators in Compile <+= generate in Protobuf,
    jniSourceFiles in Compile ++= ((managedNativeSource in Compile).value ** "*.pb.cpp").get
  )

  private def sourceGeneratorTask(
    streams: TaskStreams,
    runProtoc: Seq[String] => Int,
    managedNativeSource: File,
    sourceDirectory: File
  ) = {
    val compile = { (in: Set[File]) =>
      in.foreach(schema => streams.log.debug(s"Compiling schema $schema"))

      // Compile to C++ sources.
      compileProtoc(runProtoc, in, managedNativeSource, sourceDirectory, streams.log)

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
    runProtoc: Seq[String] => Int,
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
        runProtoc(
          Seq("-I" + sourceDirectory.absolutePath) ++ protocOptions ++ schemas.map(_.absolutePath)
        )
      } catch {
        case e: Exception =>
          throw new RuntimeException(s"error occured while compiling protobuf files: ${e.getMessage}", e)
      }
    if (exitCode != 0) {
      sys.error(s"protoc returned exit code: $exitCode")
    }
  }

}
