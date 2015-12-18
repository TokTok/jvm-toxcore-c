package sbt.tox4j

import java.io.{IOException, File}

import sbt.Keys._
import sbt._
import sbt.tox4j.logic.jni.Configure.{CompilerResult, NativeCompiler}
import sbt.tox4j.logic.jni.{BuildTool, CMakeGenerator, Configure}
import sbt.tox4j.util.NativeFinder

import scala.language.postfixOps

// scalastyle:off
object Jni extends OptionalPlugin {

  val Native = config("native")

  object Keys {

    // settings

    val libraryName = settingKey[String]("Shared library produced by JNI")

    val packageDependencies = settingKey[Seq[String]]("Dependencies from pkg-config")

    val nativeSource = settingKey[File]("JNI native sources")
    val nativeTarget = settingKey[File]("JNI native target directory")
    val managedNativeSource = settingKey[File]("Generated JNI native sources")

    val binPath = settingKey[File]("Shared libraries produced by JNI")

    val nativeCC = settingKey[CompilerResult[NativeCompiler.C]]("C compiler to use")
    val nativePrimaryCXX = settingKey[CompilerResult[NativeCompiler.Cxx]]("Primary C++ compiler to use")
    val toolchainPrefix = settingKey[Option[String]]("Optional toolchain prefix for the compiler")
    val toolchainPath = settingKey[Option[File]]("Optional toolchain location; must contain sysroot/ and bin/")
    val pkgConfigPath = settingKey[Seq[File]]("Directories to look in for pkg-config's .pc files")

    val cppFlags = settingKey[Seq[String]]("Flags to be passed to the native C and C++ compilers when compiling")
    val cFlags = settingKey[Seq[String]]("Flags to be passed to the native C compiler when compiling")
    val cxxFlags = settingKey[Seq[String]]("Flags to be passed to the native C++ compiler when compiling")
    val ldFlags = settingKey[Seq[String]]("Flags to be passed to the native compiler when linking")
    val featureTestFlags = settingKey[Seq[String]]("Feature-test flags like -DHAVE_THING if the compiler supports 'thing'")

    val coverageEnabled = settingKey[Boolean]("Whether to enable coverage instrumentation in native code")
    val coverageFlags = settingKey[Seq[String]]("Flags to be passed to the native compiler to enable coverage instrumentation")

    val buildTool = settingKey[BuildTool]("Build tool to use [make, ninja]")
    val buildFlags = settingKey[Seq[String]]("Flags to be passed to the build tool")

    val jniClasses = taskKey[Map[String, Seq[String]]]("Classes with native methods")
    val jniSourceFiles = settingKey[Seq[File]]("JNI source files")
    val jniCompile = taskKey[Seq[File]]("Compiles JNI native sources")

  }

  import Keys._

  private object PrivateKeys {

    // tasks

    val javah = taskKey[Seq[String]]("Generates JNI header files")
    val gtestPath = taskKey[Option[File]]("Finds the Google Test source path or downloads gtest from the internet")
    val tryBothCXX = taskKey[File]("Script to try two separate C++ compilers in case one crashes")
    val cmakeDependenciesFile = taskKey[File]("Generates Dependencies.cmake containing C++ dependency information")
    val cmakeCommonFile = taskKey[File]("Generates Common.cmake containing common flags and settings")
    val cmakeMainFile = taskKey[File]("Generates Main.cmake containing instructions for the main module")
    val cmakeTestFile = taskKey[Option[File]]("Generates Test.cmake containing instructions for the test module")
    val cmakeToolchainFile = taskKey[Option[File]]("Optionally generates Toolchain.cmake and returns the required cmake flags")
    val cmakeToolchainFlags = taskKey[Seq[String]]("Optionally generates Toolchain.cmake and returns the required cmake flags")
    val runCMake = taskKey[File]("Configures the build with CMake")

    // settings

    val headersPath = settingKey[File]("Generated JNI headers")
    val includes = settingKey[Seq[File]]("Compiler include directories")

  }

  import PrivateKeys._

  private val jdkHome = {
    val home = file(sys.props("java.home"))
    if (home.exists)
      Some(home.getAbsoluteFile)
    else
      None
  }

  private def jreInclude(toolchainPath: Option[File]) = {
    toolchainPath match {
      case Some(_) => None
      case None =>
        jdkHome.map { home =>
          val absHome = home.getParentFile
          // In a typical installation, JDK files are one directory above the
          // location of the JRE set in 'java.home'.
          Seq(absHome / "include")
        }
    }
  }

  private def checkExitCode(command: ProcessBuilder, log: Logger) = {
    command ! log match {
      case 0 =>
      case exitCode =>
        sys.error(s"command failed with exit code $exitCode:\n  $command")
    }
  }

  private def mkPkgConfigPath(pkgConfigPath: Seq[File], toolchainPath: Option[File]) = {
    {
      toolchainPath map (_ / "sysroot" / "usr" / "lib" / "pkgconfig") match {
        case Some(toolchainPath) =>
          if (toolchainPath.exists) {
            Seq(toolchainPath)
          } else {
            Nil
          }
        case None =>
          Nil
      }
    } ++ pkgConfigPath
  }

  private def getEnvFlags(envVar: String): Seq[String] = {
    sys.env.get(envVar).map(_.split(' ')).toSeq.flatten
  }

  override val moduleSettings = Seq(
    inConfig(Native)(Seq[Setting[_]](

      jniClasses := {
        val classes = (compileIncremental in Compile).value
          .analysis
          .relations
          .allProducts
          .filter(_.name.endsWith(".class"))
          .toSet
        NativeFinder(classes)
      },

      // Target for javah-generated headers.
      headersPath := nativeTarget.value / "include",

      // Include directories.
      includes := Nil,

      includes ++= Seq(
        headersPath.value,
        (nativeSource in Compile).value,
        (managedNativeSource in Compile).value
      ),

      includes ++= jreInclude(toolchainPath.value).getOrElse(Nil)
    )),

    Seq[Setting[_]](
      // Library name defaults to the project name.
      libraryName := name.value,

      // Initialise pkg-config dependencies to the empty sequence.
      packageDependencies := Nil,
      pkgConfigPath := sys.env.get("PKG_CONFIG_PATH").map(_.split(File.pathSeparator).toSeq.map(file)).getOrElse(Nil),

      // Native source directory defaults to "src/main/cpp".
      nativeSource in Compile := (sourceDirectory in Compile).value / "cpp",
      nativeSource in Test := (sourceDirectory in Test).value / "cpp",
      nativeTarget := (target in Compile).value / "cpp",
      managedNativeSource := nativeTarget.value / "source",

      watchSources ++= (nativeSource in Compile).value.***.get,
      watchSources ++= (nativeSource in Test).value.***.get,

      // Put the linked library in here.
      binPath := nativeTarget.value / "bin",

      // Default to global toolchain.
      toolchainPrefix := None,
      toolchainPath := None,

      // Default native C++ compiler to Clang.
      nativeCC := Configure.findCc(toolchainPath.value, toolchainPrefix.value),
      nativePrimaryCXX := Configure.findPrimaryCxx(toolchainPath.value, toolchainPrefix.value),

      // Defaults from the environment.
      cppFlags := Configure.checkCcOptions(nativeCC.value, None, getEnvFlags("CPPFLAGS")),
      cFlags := Configure.checkCcOptions(nativeCC.value, None, getEnvFlags("CFLAGS")),
      cxxFlags := Configure.checkCcOptions(nativePrimaryCXX.value, None, getEnvFlags("CXXFLAGS")),
      ldFlags := Configure.checkCcOptions(nativePrimaryCXX.value, None, getEnvFlags("LDFLAGS")),

      // Build with parallel tasks by default.
      buildTool := BuildTool.tool,
      buildFlags := Seq("-j" + java.lang.Runtime.getRuntime.availableProcessors),

      // Debug flags.
      cxxFlags ++= Configure.checkCcOptions(
        nativePrimaryCXX.value, None,
        Seq("-ggdb3"),
        Seq("-g3"),
        Seq("-g")
      ),

      // Warning flags.
      cxxFlags ++= Configure.checkCcOptions(nativePrimaryCXX.value, None, Seq("-Wall")),
      cxxFlags ++= Configure.checkCcOptions(nativePrimaryCXX.value, None, Seq("-Wextra")),
      cxxFlags ++= Configure.checkCcOptions(nativePrimaryCXX.value, None, Seq("-pedantic")),
      // TODO(iphydf): This doesn't work with g++, but try to make it work with the primary compiler anyway.
      // cxxFlags ++= Configure.checkCcOptions(nativePrimaryCXX.value, None, Seq("-fcolor-diagnostics")),

      // Use libc++ if available.
      //ccOptions ++= Configure.checkCcOptions(nativeCXX.value)(Seq("-stdlib=libc++")),

      // No RTTI and no exceptions.
      cxxFlags ++= Configure.checkCcOptions(nativePrimaryCXX.value, None, Seq("-fno-exceptions")),
      cxxFlags ++= Configure.checkCcOptions(nativePrimaryCXX.value, None, Seq("-fno-rtti")),
      cxxFlags ++= Configure.checkCcOptions(nativePrimaryCXX.value, None, Seq("-DGOOGLE_PROTOBUF_NO_RTTI")),
      cxxFlags ++= Configure.checkCcOptions(nativePrimaryCXX.value, None, Seq("-DGTEST_HAS_RTTI=0")),

      // Error on undefined references in shared object.
      ldFlags ++= Configure.checkCcOptions(nativePrimaryCXX.value, None, Seq("-Wl,-z,defs")),

      // Link librt if possible (because then it is required).
      ldFlags ++= Configure.checkCcOptions(nativePrimaryCXX.value, None, Seq("-lrt")),

      // Link with version script to avoid exporting unnecessary symbols.
      ldFlags ++= Configure.checkCcOptions(nativePrimaryCXX.value, None, {
        val versionScript = ((nativeSource in Compile).value / ("lib" + libraryName.value)).getPath + ".ver"
        Seq(s"-Wl,--version-script,$versionScript")
      }),

      // Enable test coverage collection.
      coverageEnabled := sys.env.get("TEST_GOAL").exists(_ == "coverage"),
      coverageFlags := Configure.checkCcOptions(
        nativePrimaryCXX.value, Some(cxxFlags.value),
        // TODO(iphydf): Look into making this work. It is required for "llvm-cov show".
        // Error from linker when this is enabled: multiple definition of `__llvm_profile_name__ZSt15make_error_codeSt4errc2'
        // Seq("-fprofile-instr-generate", "-fcoverage-mapping"),
        Seq("--coverage", "-DHAVE_COVERAGE"),
        Seq("-fprofile-arcs", "-ftest-coverage", "-DHAVE_COVERAGE")
      ),

      // Feature tests.
      featureTestFlags := Nil,
      featureTestFlags ++= Configure.ccFeatureTest(nativePrimaryCXX.value, cxxFlags.value, "TO_STRING", "std::to_string(3)", "iostream"),
      featureTestFlags ++= Configure.ccFeatureTest(nativePrimaryCXX.value, cxxFlags.value, "MAKE_UNIQUE", "[] { std::make_unique<int>(3); }", "memory"),

      jniSourceFiles in Compile := ((nativeSource in Compile).value ** "*").filter(Configure.isNativeSource).get,
      jniSourceFiles in Test := ((nativeSource in Test).value ** "*").filter(Configure.isNativeSource).get,

      cleanFiles ++= Seq(
        binPath.value,
        (headersPath in Native).value
      ),

      // Make shared lib available at runtime. Must be used with forked JVM to work.
      javaOptions ++= Seq(
        s"-Djava.library.path=${binPath.value}",
        "-Xmx1g",
        "-Xbatch",
        "-Xcheck:jni",
        "-Xfuture"
      ),
      initialCommands in console := "im.tox.tox4j.JavaLibraryPath.addLibraryPath(\"" + binPath.value + "\")",
      // Required in order to have a separate JVM to set Java options.
      fork := true
    ),

    inConfig(Native)(Seq[Setting[_]](
      javah := Def.task {
        val log = streams.value.log

        val classpath = (
          (dependencyClasspath in Compile).value.files ++
          Seq((classDirectory in Compile).value)
        ).mkString(File.pathSeparator)

        val jniClassNames = jniClasses.value.keys.toSeq

        val command = Seq(
          "javah",
          "-d", headersPath.value.getPath,
          "-classpath", classpath
        ) ++ jniClassNames

        if (jniClassNames.isEmpty) {
          log.info(s"No classes with native methods found; not running javah")
        } else {
          log.info(s"Running javah to generate ${jniClassNames.size} JNI headers")
          checkExitCode(command, log)
        }

        jniClassNames
      }.tag(Tags.Compile, Tags.CPU)
        .value,

      cmakeDependenciesFile := {
        CMakeGenerator.dependenciesFile(
          includes.value,
          packageDependencies.value,
          (nativeSource in Compile).value,
          nativeTarget.value,
          managedNativeSource.value
        )
      },

      cmakeCommonFile := {
        CMakeGenerator.commonFile(streams.value.log)(
          nativeTarget.value,
          cppFlags.value ++ cFlags.value ++ nativeCC.value.flags ++ nativeCC.value.sysrootFlag,
          cppFlags.value ++ cxxFlags.value ++ nativePrimaryCXX.value.flags ++ nativePrimaryCXX.value.sysrootFlag,
          ldFlags.value,
          featureTestFlags.value,
          coverageEnabled.value,
          coverageFlags.value
        )
      },

      cmakeMainFile := {
        CMakeGenerator.mainFile(
          binPath.value,
          libraryName.value,
          nativeTarget.value,
          (jniSourceFiles in Compile).value
        )
      },

      gtestPath := {
        val log = streams.value.log

        val candidates = Seq(
          file("/usr/src/gtest")
        )

        candidates find { candidate =>
          (candidate / "src" / "gtest-all.cc").exists
        } match {
          case Some(gtestDir) => Some(gtestDir)
          case None =>
            val gtestDir = managedNativeSource.value / "gtest"
            if (!gtestDir.exists) {
              val command = Seq(
                "svn", "checkout",
                "http://googletest.googlecode.com/svn/trunk/",
                gtestDir.getPath
              )

              log.info("Fetching gtest sources")
              try {
                command ! log match {
                  case 0 =>
                    Some(gtestDir)
                  case exitCode =>
                    log.info(s"command failed with exit code $exitCode:\n  $command")
                    None
                }
              } catch {
                case e: IOException =>
                  log.info(s"command could not be run (${e.getMessage}):\n  $command")
                  None
              }
            } else {
              Some(gtestDir)
            }
        }
      },

      cmakeTestFile := {
        gtestPath.value.map(CMakeGenerator.testFile).map(_(
          libraryName.value,
          nativeTarget.value,
          (jniSourceFiles in Test).value
        ))
      },

      cmakeToolchainFile := {
        toolchainPath.value.map(CMakeGenerator.toolchainFile).map(_(
          nativeTarget.value,
          nativeCC.value,
          Configure.findSecondaryCxx(toolchainPath.value, toolchainPrefix.value).headOption match {
            case None => nativePrimaryCXX.value
            case Some(secondaryCxx) =>
              nativePrimaryCXX.value.copy(
                compiler = NativeCompiler.Cxx {
                  CMakeGenerator.tryBothCxxScript(
                    nativeTarget.value,
                    nativePrimaryCXX.value,
                    secondaryCxx
                  ).getPath
                }
              )
          }
        ))
      },

      cmakeToolchainFlags := {
        val flags = for {
          toolchainPath <- toolchainPath.value
          cmakeToolchainFile <- cmakeToolchainFile.value
        } yield {
          val jniPath = toolchainPath / "sysroot" / "usr" / "include"
          if (!(jniPath / "jni.h").exists) {
            sys.error("JNI path does not contain jni.h: " + jniPath)
          }

          val needJniMd =
            if ((jniPath / "jni_md.h").exists)
              "y"
            else
              "n"

          Seq(
            "-DCMAKE_TOOLCHAIN_FILE=" + cmakeToolchainFile,
            "-DJNI_H=" + jniPath,
            "-DNEED_JNI_MD=" + needJniMd
          )
        }

        flags getOrElse Nil
      },

      runCMake := Def.task {
        val log = streams.value.log

        // Make sure the output directory exists.
        binPath.value.mkdirs()

        val pkgConfigDirs =
          mkPkgConfigPath(pkgConfigPath.value, toolchainPath.value).mkString(File.pathSeparator)

        val buildPath = nativeTarget.value / "_build"
        buildPath.mkdirs()

        Configure.configLog.info(s"PKG_CONFIG_PATH = $pkgConfigDirs")
        val env = (toolchainPath.value match {
          case None =>
            Seq(
              ("CC", nativeCC.value.compiler.program),
              ("CXX", nativePrimaryCXX.value.compiler.program)
            )

          case Some(toolchainPath) =>
            Seq(
              ("PATH",
                sys.env("PATH") +
                File.pathSeparator +
                (toolchainPath / "bin"))
            )
        }) ++ Seq(
          ("CFLAGS", nativeCC.value.sysrootFlag.getOrElse("")),
          ("CXXFLAGS", nativePrimaryCXX.value.sysrootFlag.getOrElse("")),
          ("PKG_CONFIG_PATH", pkgConfigDirs)
        ) ++ javaHome.value.map(path => ("JAVA_HOME", path.getPath))

        val flags = cmakeToolchainFlags.value

        val cmake = {
          Process(
            Seq(
              "cmake", "-G" + buildTool.value.name,
              "-DDEPENDENCIES_FILE=" + cmakeDependenciesFile.value,
              "-DCOMMON_FILE=" + cmakeCommonFile.value,
              "-DMAIN_FILE=" + cmakeMainFile.value,
              baseDirectory.value.getPath
            ) ++ flags ++ cmakeTestFile.value.map("-DTEST_FILE=" + _),
            buildPath,
            env: _*
          )
        }

        log.info("Configuring C++ build")
        checkExitCode(cmake, log)

        buildPath
      }.dependsOn(javah)
        .tag(Tags.Compile, Tags.CPU)
        .value,

      jniCompile := Def.task {
        val log = streams.value.log

        val buildPath = runCMake.value
        val mainSources = (jniSourceFiles in Compile).value

        val command = Process(buildTool.value.command +: buildFlags.value, buildPath)

        log.info(s"Compiling ${mainSources.size} C++ sources to ${binPath.value}")
        checkExitCode(command, log)

        Seq("dll", "dylib", "so").map("*." + _).map(binPath.value ** _).flatMap(_.get)
      }.dependsOn(javah)
        .tag(Tags.Compile, Tags.CPU)
        .value
    )),

    Seq[Setting[_]](
      (compile in Compile) <<= (compile in Compile).dependsOn(jniCompile in Native),
      (test in Test) <<= (test in Test).dependsOn(jniCompile in Native)
    )
  ).flatten

}
