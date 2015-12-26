package im.tox.sbt

import im.tox.sbt.ConfigurePlugin.Configurations._
import im.tox.sbt.ConfigurePlugin.Keys._
import im.tox.sbt.NativeCompilePlugin.Keys._
import im.tox.sbt.PkgConfigPlugin.autoImport._
import sbt.Keys._
import sbt._

object AndroidNdkPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = noTrigger
  // Load after PkgConfigPlugin so we can remove -lpthread and -L flags.
  override def requires: Plugins = NativeCompilePlugin && PkgConfigPlugin

  object Keys {
    val ndkHome = settingKey[File]("Android NDK home.")
    val toolchainHome = settingKey[File]("Android toolchain directory.")
  }

  import Keys._

  val androidSettings = Seq(
    // Hack to make "publishLocal" build the native library. Since tests don't run when building for Android,
    // there is otherwise no reason to build the library.
    publishLocal <<= publishLocal.dependsOn(nativeLink in NativeCompile),

    ndkHome := sys.env.get("ANDROID_NDK_HOME").map(file).filter(_.exists).getOrElse(file(sys.env("HOME")) / "android-ndk"),
    toolchainHome := baseDirectory.value.getParentFile.getParentFile / "toolchains" / crossPlatform.value,

    cc1 := cc1.value.copy(program = (toolchainHome.value / "bin" / (crossPlatform.value + "-clang")).getPath),
    cc2 := cc1.value.copy(program = (toolchainHome.value / "bin" / (crossPlatform.value + "-gcc")).getPath),
    cxx1 := cxx1.value.copy(program = (toolchainHome.value / "bin" / (crossPlatform.value + "-clang++")).getPath),
    cxx2 := cxx1.value.copy(program = (toolchainHome.value / "bin" / (crossPlatform.value + "-g++")).getPath),

    // Ignore all flags from the environment variables.
    commonEnvFlags := Nil,
    cEnvFlags := Nil,
    cxxEnvFlags := Nil,
    ldEnvFlags := Nil,

    sources in NativeCompile += ndkHome.value / "sources/android/cpufeatures/cpu-features.c",

    pkgConfigPath := (toolchainHome.value / "sysroot" / "usr" / "lib" / "pkgconfig").getPath,
    jniIncludeFlags := Nil,

    commonEnvFlags += "--sysroot=" + (toolchainHome.value / "sysroot").getPath,
    ldEnvFlags += "--sysroot=" + (toolchainHome.value / "sysroot").getPath,

    ldConfigFlags += "-Wl,-z,defs",
    ldConfigFlags += "-latomic",

    ldConfigFlags <<= ldConfigFlags map {
      _.filterNot(flag => flag == "-lpthread" || flag.startsWith("-L"))
    }
  )

  override def projectSettings: Seq[Setting[_]] = androidSettings

}
