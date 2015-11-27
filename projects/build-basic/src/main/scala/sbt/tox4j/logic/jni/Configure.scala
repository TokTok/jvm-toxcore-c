package sbt.tox4j.logic.jni

import java.io.{File, IOException, PrintWriter}

import org.apache.commons.io.{FilenameUtils, IOUtils}

import scala.collection.JavaConverters._

object Configure {

  object configLog extends AnyRef with sbt.ProcessLogger {
    val logFile = new PrintWriter("config.log")

    def buffer[T](f: => T): T = f

    def error(s: => String): Unit = {
      logFile.println("[error] " + s)
      logFile.flush()
    }
    def info(s: => String): Unit = {
      logFile.println("[info] " + s)
      logFile.flush()
    }
  }

  sealed trait NativeCompiler {
    def program: String
  }
  object NativeCompiler {
    final case class C(program: String) extends NativeCompiler
    final case class Cxx(program: String) extends NativeCompiler
  }

  /**
   * Extensions of source files.
   */
  private val cExtensions = Seq(".c")
  private val cxxExtensions = Seq(".cpp", ".cc", ".cxx", ".C")

  private def extensions(nativeCompiler: NativeCompiler): Seq[String] = {
    nativeCompiler match {
      case NativeCompiler.C(_)   => cExtensions
      case NativeCompiler.Cxx(_) => cxxExtensions
    }
  }

  def isNativeSource(file: File): Boolean = {
    file.isFile && (cxxExtensions ++ cExtensions).exists(file.getName.toLowerCase.endsWith)
  }

  final case class CompilerResult[T <: NativeCompiler](
    compiler: T,
    code: String,
    flags: Seq[String],
    sysroot: Option[File],
    sysrootFlag: Option[String],
    output: String,
    success: Boolean
  )

  private def checkCompilerResult[T <: NativeCompiler](
    compiler: T,
    code: String,
    flags: Seq[String],
    extraFlags: Seq[String],
    sysroot: Option[File]
  ): Option[CompilerResult[T]] = {
    val sourceFile = File.createTempFile("configtest", extensions(compiler).head); sourceFile.deleteOnExit()
    val targetFile = File.createTempFile("configtest", ".out"); targetFile.deleteOnExit()
    val gcnoFile = new File(FilenameUtils.removeExtension(sourceFile.getName) + ".gcno"); gcnoFile.deleteOnExit()

    try {
      val out = new PrintWriter(sourceFile)
      try {
        out.println(code)
        out.println("int main () { return 0; }")
      } finally {
        out.close()
      }

      val sysrootFlag = sysroot.map("--sysroot=" + _.getPath)

      val command = (
        compiler.program +: sourceFile.getPath +: "-o" +: targetFile.getPath +:
        (flags ++ extraFlags)
      ) ++ sysrootFlag

      val process = new ProcessBuilder(command: _*)
        .redirectErrorStream(true)
        .start()

      val result = CompilerResult(
        compiler,
        code,
        flags,
        sysroot,
        sysrootFlag,
        IOUtils.readLines(process.getInputStream).asScala.mkString("\n"),
        process.waitFor() == 0
      )

      if (result.success) {
        configLog.info("Success: " + result.toString)
      } else {
        configLog.info("Failure: " + result.toString)
      }
      Some(result)
    } catch {
      case exn: IOException =>
        configLog.info(exn.getMessage)
        None
    } finally {
      targetFile.delete()
      sourceFile.delete()
      gcnoFile.delete()
    }
  }

  private def makeToolchainCandidates(
    toolchainPath: Option[File],
    toolchainPrefix: Option[String],
    tools: Seq[String]
  ): Seq[String] = {
    import sbt._

    val toolchainTools =
      for {
        toolchainPath <- toolchainPath
        toolchainPrefix <- toolchainPrefix
      } yield {
        val prefixedTools = tools.map(tool => s"$toolchainPrefix-$tool") ++ tools
        (prefixedTools map { tool => (toolchainPath / "bin" / tool).getPath }) ++ tools
      }
    toolchainTools.getOrElse(tools)
  }

  def findTool[T <: NativeCompiler](
    required: Boolean,
    makeTool: String => T,
    toolchainPath: Option[File],
    toolchainPrefix: Option[String],
    tools: Seq[String],
    code: String,
    flagsCandidates: Seq[Seq[String]]
  ): Seq[CompilerResult[T]] = {
    import sbt._

    val toolchainCandidates = makeToolchainCandidates(
      toolchainPath,
      toolchainPrefix,
      tools
    )

    val sysroot = toolchainPath.map(_ / "sysroot")

    configLog.info("Trying to find compiler; candidates are: " + toolchainCandidates)
    val results =
      for {
        candidate <- toolchainCandidates
        flagsCandidate <- flagsCandidates
        result <- checkCompilerResult(
          makeTool(candidate),
          code,
          flagsCandidate,
          Nil,
          sysroot
        )
      } yield {
        result
      }

    if (required) {
      val result = results.find(_.success).getOrElse {
        sys.error("Could not find a viable compiler; attempts: " + results)
      }

      configLog.info("Selected tool: " + result)
    }
    results.filter(_.success)
  }

  def findCc(toolchainPath: Option[File], toolchainPrefix: Option[String]): CompilerResult[NativeCompiler.C] = {
    findTool(
      required = true,
      NativeCompiler.C,
      toolchainPath,
      toolchainPrefix,
      sys.env.get("CC").toSeq ++ Seq("clang-3.6", "clang-3.5", "clang35", "gcc-4.9", "clang", "gcc", "cc"),
      "typeof(0) i = 0;",
      Seq(
        Seq("-std=gnu89")
      )
    ).head
  }

  def findPrimaryCxx(toolchainPath: Option[File], toolchainPrefix: Option[String]): CompilerResult[NativeCompiler.Cxx] = {
    findTool(
      required = true,
      NativeCompiler.Cxx,
      toolchainPath,
      toolchainPrefix,
      sys.env.get("CXX").toSeq ++ Seq("clang++-3.6", "clang++-3.5", "clang35++", "g++-4.9", "clang++", "g++", "c++"),
      """
        |auto f = [](auto i) mutable { return i; };
        |
        |template<typename... Args>
        |int bar (Args ...args) { return sizeof... (Args); }
        |
        |template<typename T>
        |extern char const *x;
        |
        |template<>
        |char const *x<int> = "int";
        |
        |template<typename... Args>
        |auto foo (Args ...args) {
        |  return [&] { return bar (args...); };
        |}
        |""".stripMargin,
      Seq(
        Seq("-std=c++14"),
        Seq("-std=c++1y")
      )
    ).head
  }

  /**
   * Find another C++ compiler that can compile most of the code and can be used as a fallback in case the
   * primary one crashes.
   */
  def findSecondaryCxx(
    toolchainPath: Option[File],
    toolchainPrefix: Option[String]
  ): Seq[CompilerResult[NativeCompiler.Cxx]] = {
    findTool(
      required = false,
      NativeCompiler.Cxx,
      toolchainPath,
      toolchainPrefix,
      sys.env.get("CXX").toSeq ++ Seq("clang++-3.6", "clang++-3.5", "clang35++", "g++-4.9", "clang++", "g++", "c++"),
      """
        |auto f = [](auto i) mutable { return i; };
        |
        |template<typename... Args>
        |int bar (Args ...args) { return sizeof... (Args); }
        |
        |#include <memory>
        |void foo() { std::make_unique<int>(3); }
        |""".stripMargin,
      Seq(
        Seq("-std=c++14"),
        Seq("-std=c++1y")
      )
    )
  }

  def checkCcOptions[T <: NativeCompiler](
    primaryCompiler: CompilerResult[T],
    cxxFlags: Option[Seq[String]],
    flagsCandidates: Seq[String]*
  ): Seq[String] = {
    val results =
      for {
        flagsCandidate <- flagsCandidates
        result <- checkCompilerResult(
          primaryCompiler.compiler,
          primaryCompiler.code,
          flagsCandidate,
          cxxFlags.getOrElse(Nil) ++ primaryCompiler.flags,
          primaryCompiler.sysroot
        )
      } yield {
        result
      }

    val result = results.find(_.success).toSeq.flatMap(_.flags)
    if (result.isEmpty) {
      configLog.info("checkCcOptions was unable to find valid options: " + results)
    } else {
      configLog.info("checkCcOptions found valid options: " + result)
    }

    result
  }

  def ccFeatureTest[T <: NativeCompiler](
    compilerResult: CompilerResult[T],
    cxxFlags: Seq[String],
    flag: String,
    code: String,
    headers: String*
  ): Seq[String] = {
    checkCcOptions(
      compilerResult.copy(
        code = headers.map("#include <" + _ + ">").mkString("\n") + "\n" +
        s"void configtest() { $code; }"
      ),
      Some(cxxFlags),
      Seq(s"-DHAVE_$flag")
    )
  }

}
