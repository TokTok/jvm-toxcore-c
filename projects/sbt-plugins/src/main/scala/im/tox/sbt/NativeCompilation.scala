package im.tox.sbt

import im.tox.sbt.ConfigurePlugin.NativeCompiler
import org.apache.commons.io.FilenameUtils
import sbt.Keys.TaskStreams
import sbt._

import scala.util.control.NonFatal

/**
 * TODO(iphydf): Write comments.
 */
object NativeCompilation {

  val ccFileFilter = "*.c"
  val cxxFileFilter = "*.cc" | "*.cpp"
  val sourceFileFilter = ccFileFilter | cxxFileFilter
  val headerFileFilter = "*.h" | "*.hh" | "*.hpp"

  private def runCompiler(log: Logger, compiler: NativeCompiler, arguments: Seq[String]): String = {
    val command = compiler.program +: arguments
    log.debug(command.mkString(" "))

    try {
      command !! log
    } catch {
      case NonFatal(e) =>
        log.error("Error while executing: " + command.mkString(" "))
        sys.exit(1)
    }
  }

  private def compileSource(
    log: Logger,
    sourceDirectories: Seq[File],
    objectDirectory: File
  )(
    compiler: NativeCompiler,
    flags: Seq[String]
  )(
    sourceFile: File
  ): Def.Initialize[Task[File]] = Def.task {
    val relativeFile = sourceDirectories.flatMap(_.relativize(sourceFile)).headOption match {
      case None           => file(sourceFile.getName)
      case Some(relative) => relative
    }

    val objectFile = objectDirectory / (FilenameUtils.removeExtension(relativeFile.getPath) + ".o")
    objectFile.getParentFile.mkdirs()

    val arguments = Seq(
      "-c",
      "-o", objectFile.getPath,
      sourceFile.getPath
    ) ++ flags

    runCompiler(log, compiler, arguments)

    objectFile
  }

  def compileSources(
    log: Logger,
    cc: NativeCompiler, cflags: Seq[String],
    cxx: NativeCompiler, cxxflags: Seq[String],
    sourceDirectories: Seq[File],
    objectDirectory: File,
    sources: Seq[File]
  ): Def.Initialize[Task[Seq[File]]] = {
    if (sources.nonEmpty) {
      log.info(s"Compiling ${sources.length} C/C++ sources")
      log.info(s"CFLAGS   = $cflags")
      log.info(s"CXXFLAGS = $cxxflags")
    }

    val compileWith = compileSource(
      log,
      sourceDirectories,
      objectDirectory
    ) _

    val ccObjects = sources.filter(ccFileFilter.accept).map(compileWith(cc, cflags))
    val cxxObjects = sources.filter(cxxFileFilter.accept).map(compileWith(cxx, cxxflags))
    (ccObjects ++ cxxObjects).joinWith(_.join)
  }

  def linkSharedLibrary(
    streams: TaskStreams,
    linker: NativeCompiler, ldflags: Seq[String],
    nativeObjects: Seq[File],
    nativeLibraryOutput: File
  ): Option[File] = {
    if (nativeObjects.isEmpty) {
      None
    } else {
      val log = streams.log
      log.info(s"Linking shared library $nativeLibraryOutput")
      log.info(s"LDFLAGS = $ldflags")

      val arguments = Seq(
        "-shared",
        "-o", nativeLibraryOutput.getPath
      ) ++ nativeObjects.map(_.getPath) ++ ldflags

      runCompiler(log, linker, arguments)

      JavaLibraryPath.addLibraryPath(nativeLibraryOutput.getParent)
      Some(nativeLibraryOutput)
    }
  }

  def linkProgram(
    streams: TaskStreams,
    linker: NativeCompiler, ldflags: Seq[String],
    nativeObjects: Seq[File],
    nativeLibraryOutput: Option[File],
    nativeProgramOutput: File
  ): Option[File] = {
    if (nativeObjects.isEmpty) {
      None
    } else {
      val log = streams.log
      log.info(s"Linking program $nativeProgramOutput")
      log.info(s"LDFLAGS = $ldflags")

      val arguments = Seq(
        "-o", nativeProgramOutput.getPath
      ) ++ nativeObjects.map(_.getPath) ++ ldflags

      runCompiler(log, linker, arguments)

      Some(nativeProgramOutput)
    }
  }

}
