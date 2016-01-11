package im.tox.sbt

import sbt.Keys._
import sbt._
import sbt.plugins.IvyPlugin

object ConfigurePlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = IvyPlugin

  sealed trait Language { def suffix: String }
  case object C extends Language { def suffix: String = "c" }
  case object Cxx extends Language { def suffix: String = "cpp" }

  final case class Stdlib(flags: Seq[String])
  final case class NativeCompiler[L <: Language](language: L, program: String)

  final case class NativeCompilationSettings[L <: Language](
    compiler1: NativeCompiler[L],
    compiler2: NativeCompiler[L],
    flags: Seq[String]
  )

  object autoImport {
    val NativeCompile = Configurations.NativeCompile
    val NativeTest = Configurations.NativeTest
  }

  object Configurations {
    val NativeCompile = config("native-compile") extend Compile
    val NativeTest = config("native-test") extend (Test, NativeCompile)
  }

  import Configurations._

  object Keys {
    val cc1 = taskKey[NativeCompiler[C.type]]("C compiler (primary).")
    val cc2 = taskKey[NativeCompiler[C.type]]("C compiler (fallback).")
    val cxx1 = taskKey[NativeCompiler[Cxx.type]]("C++ compiler (primary).")
    val cxx2 = taskKey[NativeCompiler[Cxx.type]]("C++ compiler (fallback).")

    val stdlib = taskKey[Stdlib]("Standard library to use")
    val jniIncludeFlags = taskKey[Seq[String]]("Common C/C++ compiler flags for JNI includes (jni.h/jni_md.h).")
    val commonEnvFlags = taskKey[Seq[String]]("Common C/C++ compiler flags from CPPFLAGS.")
    val commonConfigFlags = taskKey[Seq[String]]("Common C/C++ compiler flags from Configure.")
    val cConfigFlags = taskKey[Seq[String]]("C compiler flags from Configure.")
    val cEnvFlags = taskKey[Seq[String]]("C compiler flags from CFLAGS.")
    val cFeatureFlags = taskKey[Seq[String]]("C compiler flags for feature tests.")
    val cxxConfigFlags = taskKey[Seq[String]]("C++ compiler flags from Configure.")
    val cxxEnvFlags = taskKey[Seq[String]]("C++ compiler flags from CXXFLAGS.")
    val cxxFeatureFlags = taskKey[Seq[String]]("C++ compiler flags for feature tests.")
    val ldConfigFlags = taskKey[Seq[String]]("C++ linker flags from Configure.")
    val ldEnvFlags = taskKey[Seq[String]]("C++ linker flags from LDFLAGS.")

    val cFlags = taskKey[Seq[String]]("Combined C compiler flags.")
    val cxxFlags = taskKey[Seq[String]]("Combined C++ compiler flags.")
    val ldFlags = taskKey[Seq[String]]("Combined linker flags.")
  }

  import Keys._

  def osName: String = {
    sys.props("os.name") match {
      case "Linux"    => "linux"
      case "Mac OS X" => "darwin"
    }
  }

  def archName: String = {
    sys.props("os.arch") match {
      case "amd64" | "x86_64" => "x86_64"
    }
  }

  val jdkHome = file(sys.props("java.home")).getParentFile

  def getEnvFlags(envVar: String): Seq[String] = {
    sys.env.get(envVar).map(_.split("\\s+")).toList.flatten
  }

  val globalConfig = Seq(
    cc1 := Configure.findCompiler(streams.value.log, C,
      sys.env.getOrElse("CC", "clang"),
      "clang-3.8",
      "clang-3.7",
      "clang-3.6",
      "clang-3.5"),
    cc2 := NativeCompiler(C, "gcc"),
    cxx1 := Configure.findCompiler(streams.value.log, Cxx,
      sys.env.getOrElse("CXX", "clang++"),
      "clang++-3.8",
      "clang++-3.7",
      "clang++-3.6",
      "clang++-3.5"),
    cxx2 := NativeCompiler(Cxx, "g++"),

    jniIncludeFlags := Seq(
      "-I" + (jdkHome / "include"),
      "-I" + (jdkHome / "include" / osName)
    ),

    commonConfigFlags := Nil,
    cConfigFlags := Nil,
    cxxConfigFlags := Nil,
    ldConfigFlags := Nil,
    cFeatureFlags := Nil,
    cxxFeatureFlags := Nil,

    commonEnvFlags := Configure.tryCompile(streams.value.log, cxx1.value, getEnvFlags("CPPFLAGS")),
    cEnvFlags := Configure.tryCompile(streams.value.log, cxx1.value, getEnvFlags("CFLAGS")),
    cxxEnvFlags := Configure.tryCompile(streams.value.log, cxx1.value, getEnvFlags("CXXFLAGS")),
    ldEnvFlags := Configure.tryCompile(streams.value.log, cxx1.value, getEnvFlags("LDFLAGS")),

    stdlib := {
      val libcxx = Configure.tryCompile(streams.value.log, cxx1.value, Seq("-stdlib=libc++"))
      if (libcxx.nonEmpty) {
        Stdlib(libcxx)
      } else {
        val libstdcxx = Configure.tryCompile(streams.value.log, cxx1.value, Seq("-stdlib=libstdc++"))
        Stdlib(libstdcxx)
      }
    },

    cxxConfigFlags <++= stdlib map { _.flags },
    ldConfigFlags <++= stdlib map { _.flags },

    // Optimisations and debugging.
    // commonConfigFlags ++= Configure.tryCompile(streams.value.log, cxx.value, Seq("-O3")),
    commonConfigFlags ++= Configure.tryCompile(streams.value.log, cxx1.value, Seq("-ggdb3")),

    // Colourful error messages.
    commonConfigFlags ++= Configure.tryCompile(streams.value.log, cxx1.value, Seq("-Wall")),
    commonConfigFlags ++= Configure.tryCompile(streams.value.log, cxx1.value, Seq("-Wextra")),
    commonConfigFlags ++= Configure.tryCompile(streams.value.log, cxx1.value, Seq("-pedantic")),
    commonConfigFlags ++= Configure.tryCompile(streams.value.log, cxx1.value, Seq("-fcolor-diagnostics")),

    // Needed for shared libraries on some platforms.
    commonConfigFlags ++= Configure.tryCompile(streams.value.log, cxx1.value, Seq("-fPIC")),

    // Error on undefined references in shared object.
    ldConfigFlags ++= Configure.tryCompile(streams.value.log, cxx1.value, Seq("-Wl,-z,defs")),

    // Link librt if possible (because then it is required).
    ldConfigFlags ++= Configure.tryCompile(streams.value.log, cxx1.value, Seq("-lrt")),

    // No RTTI and no exceptions.
    cxxConfigFlags ++= Configure.tryCompile(streams.value.log, cxx1.value, Seq("-fno-exceptions")),
    cxxConfigFlags ++= Configure.tryCompile(streams.value.log, cxx1.value, Seq("-fno-rtti")),
    cxxConfigFlags ++= Configure.tryCompile(streams.value.log, cxx1.value, Seq("-DGOOGLE_PROTOBUF_NO_RTTI")),
    cxxConfigFlags ++= Configure.tryCompile(streams.value.log, cxx1.value, Seq("-DGTEST_HAS_RTTI=0")),

    cConfigFlags ++= Configure.tryCompile(streams.value.log, cc1.value,
      Seq("-std=gnu99"),
      Seq("-std=c99")),

    cxxConfigFlags ++= Configure.tryCompile(streams.value.log, cxx1.value,
      Seq("-std=c++14"),
      Seq("-std=c++1y")),

    cxxFeatureFlags ++= Configure.tryCompileIncludes(streams.value.log, cxx1.value, cxxConfigFlags.value ++ ldConfigFlags.value,
      Seq("stdio.h"), "using ::gets;", "-DHAVE_GETS"),
    cxxFeatureFlags ++= Configure.tryCompileIncludes(streams.value.log, cxx1.value, cxxConfigFlags.value ++ ldConfigFlags.value,
      Seq("memory"), "using std::make_unique;", "-DHAVE_MAKE_UNIQUE"),
    cxxFeatureFlags ++= Configure.tryCompileIncludes(streams.value.log, cxx1.value, cxxConfigFlags.value ++ ldConfigFlags.value,
      Seq("string"), "using std::to_string;", "-DHAVE_TO_STRING"),

    cxxFeatureFlags += "-includecpp14compat.h"
  )

  val localConfig = Seq(
    cFlags := commonEnvFlags.value ++ cEnvFlags.value ++ commonConfigFlags.value ++ cConfigFlags.value ++ cFeatureFlags.value,
    cxxFlags := commonEnvFlags.value ++ cxxEnvFlags.value ++ commonConfigFlags.value ++ cxxConfigFlags.value ++ cxxFeatureFlags.value,

    commonConfigFlags in NativeCompile <<= commonConfigFlags in Default,
    cConfigFlags in NativeCompile <<= cConfigFlags in Default,
    cxxConfigFlags in NativeCompile <<= cxxConfigFlags in Default,
    ldConfigFlags in NativeCompile <<= ldConfigFlags in Default,

    commonConfigFlags in NativeTest <<= commonConfigFlags in NativeCompile,
    cConfigFlags in NativeTest <<= cConfigFlags in NativeCompile,
    cxxConfigFlags in NativeTest <<= cxxConfigFlags in NativeCompile,
    ldConfigFlags in NativeTest <<= ldConfigFlags in NativeCompile
  )

  override def projectSettings: Seq[Setting[_]] =
    globalConfig ++
      inConfig(NativeCompile)(localConfig) ++
      inConfig(NativeTest)(localConfig)

}
