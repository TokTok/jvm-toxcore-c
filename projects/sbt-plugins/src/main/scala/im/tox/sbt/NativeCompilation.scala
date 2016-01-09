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
        log.error(s"Error ($e) while executing: " + command.mkString(" "))
        throw e
    }
  }

  private def doCompile(
    log: Logger,
    sourceDirectories: Seq[File],
    objectDirectory: File,
    compiler1: NativeCompiler,
    compiler2: NativeCompiler,
    flags: Seq[String]
  )(
    sourceFile: File
  ): File = {
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

    try {
      runCompiler(log, compiler1, arguments)
    } catch {
      case NonFatal(e) =>
        val argumentsFallback = arguments.filterNot { flag =>
          // GCC doesn't understand these.
          flag == "-fcolor-diagnostics" || flag.startsWith("-stdlib=")
        }
        runCompiler(log, compiler2, argumentsFallback)
    }

    objectFile
  }

  private def compileSource(
    log: Logger,
    cacheDirectory: File,
    sourceDirectories: Seq[File],
    objectDirectory: File
  )(
    compiler1: NativeCompiler,
    compiler2: NativeCompiler,
    flags: Seq[String]
  )(
    sourceFile: File
  ): Def.Initialize[Task[File]] = Def.task {
    FileFunction.cached(
      cacheDirectory / sourceFile.getName,
      inStyle = FilesInfo.lastModified,
      outStyle = FilesInfo.exists
    ) { inputs =>
        Set(doCompile(log, sourceDirectories, objectDirectory, compiler1, compiler2, flags)(inputs.head))
      }(Set(sourceFile)).head
  }

  def compileSources(
    log: Logger, cacheDirectory: File,
    cc1: NativeCompiler, cc2: NativeCompiler, cflags: Seq[String],
    cxx1: NativeCompiler, cxx2: NativeCompiler, cxxflags: Seq[String],
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
      log, cacheDirectory,
      sourceDirectories,
      objectDirectory
    ) _

    val ccObjects = sources.filter(ccFileFilter.accept).map(compileWith(cc1, cc2, cflags))
    val cxxObjects = sources.filter(cxxFileFilter.accept).map(compileWith(cxx1, cxx2, cxxflags))
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
      JavaLibraryPath.addLibraryPath(nativeLibraryOutput.getParent)

      FileFunction.cached(
        streams.cacheDirectory / nativeLibraryOutput.getName,
        inStyle = FilesInfo.hash,
        outStyle = FilesInfo.exists
      ) { nativeObjects =>
          val log = streams.log
          log.info(s"Linking shared library $nativeLibraryOutput")
          log.info(s"LDFLAGS = $ldflags")

          val arguments = Seq(
            "-shared",
            "-o", nativeLibraryOutput.getPath
          ) ++ nativeObjects.map(_.getPath) ++ ldflags

          runCompiler(log, linker, arguments)
          Set(nativeLibraryOutput)
        }(nativeObjects.toSet).headOption
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
      FileFunction.cached(
        streams.cacheDirectory / nativeProgramOutput.getName,
        inStyle = FilesInfo.hash,
        outStyle = FilesInfo.exists
      ) { nativeObjects =>

          val log = streams.log
          log.info(s"Linking program $nativeProgramOutput")
          log.info(s"LDFLAGS = $ldflags")

          val arguments = Seq(
            "-o", nativeProgramOutput.getPath
          ) ++ nativeObjects.map(_.getPath) ++ ldflags

          runCompiler(log, linker, arguments)
          Set(nativeProgramOutput)
        }(nativeObjects.toSet).headOption
    }
  }

}
