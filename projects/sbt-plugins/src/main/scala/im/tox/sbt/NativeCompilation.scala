package im.tox.sbt

import im.tox.sbt.ConfigurePlugin.{Cxx, NativeCompilationSettings, C, NativeCompiler}
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

  private def runCompiler(log: Logger, compiler: NativeCompiler[_], arguments: Seq[String]): String = {
    val command = compiler.program +: (compiler.flags ++ arguments)
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
    settings: NativeCompilationSettings[_]
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
    ) ++ settings.flags

    runCompiler(log, settings.compiler, arguments)

    objectFile
  }

  private def compileSource(
    log: Logger,
    cacheDirectory: File,
    sourceDirectories: Seq[File],
    objectDirectory: File
  )(
    settings: NativeCompilationSettings[_]
  )(
    sourceFile: File
  ): Def.Initialize[Task[File]] = Def.task {
    FileFunction.cached(
      cacheDirectory / sourceFile.getName,
      inStyle = FilesInfo.lastModified,
      outStyle = FilesInfo.exists
    ) { inputs =>
        Set(doCompile(log, sourceDirectories, objectDirectory, settings)(inputs.head))
      }(Set(sourceFile)).head
  }

  def compileSources(
    log: Logger, cacheDirectory: File,
    cc: NativeCompilationSettings[C.type],
    cxx: NativeCompilationSettings[Cxx.type],
    sourceDirectories: Seq[File],
    objectDirectory: File,
    sources: Seq[File]
  ): Def.Initialize[Task[Seq[File]]] = {
    if (sources.nonEmpty) {
      log.info(s"Compiling ${sources.length} C/C++ sources")
      log.info(s"CC       = ${cc.compiler}")
      log.info(s"CXX      = ${cxx.compiler}")
      log.info(s"CFLAGS   = ${cc.flags}")
      log.info(s"CXXFLAGS = ${cxx.flags}")
    }

    val compileWith = compileSource(
      log, cacheDirectory,
      sourceDirectories,
      objectDirectory
    ) _

    val ccObjects = sources.filter(ccFileFilter.accept).map(compileWith(cc))
    val cxxObjects = sources.filter(cxxFileFilter.accept).map(compileWith(cxx))
    (ccObjects ++ cxxObjects).joinWith(_.join)
  }

  def linkSharedLibrary(
    streams: TaskStreams,
    linker: NativeCompiler[_], ldflags: Seq[String],
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
    linker: NativeCompiler[_], ldflags: Seq[String],
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
