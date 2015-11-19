package sbt.tox4j.logic.jni

import java.io.File

import sbt.Keys._
import sbt._
import sbt.tox4j.Jni.Keys._

sealed trait Platform

object Platform {

  final case class Android(platform: String) extends Platform
  case object Host extends Platform

  // Android build:
  private def androidSettings(platform: String) = Seq(
    toolchainPrefix := Some(platform),
    toolchainPath := {
      val candidates =
        // Try $TOOLCHAIN first, then try some other possible candidate paths.
        sys.env.get("TOOLCHAIN").map(file) ++
          Seq(
            baseDirectory.value.getParentFile.getParentFile / "toolchains" / platform,
            baseDirectory.value / "android" / platform,
            baseDirectory.value.getParentFile / "android" / platform
          )
      candidates.find { candidate =>
        Configure.configLog.info(s"Toolchain path '$candidate' exists? ${candidate.exists}")
        candidate.exists
      }
    },
    pkgConfigPath := toolchainPath.value.map(_ / "sysroot/usr/lib/pkgconfig").toSeq,
    ldFlags := Nil,

    jniSourceFiles in Compile += {
      def ifExists(file: File): Option[File] = {
        Option(file).filter(_.exists)
      }

      val home = file(sys.env("HOME"))

      val candidates = Seq(
        sys.env.get("ANDROID_NDK_HOME").map(file).flatMap(ifExists),
        ifExists(home / "usr/android-ndk"),
        ifExists(home / "android-ndk")
      )

      candidates.find(_.nonEmpty).flatten match {
        case Some(ndkHome) =>
          val cpufeatures = ndkHome / "sources/android/cpufeatures/cpu-features.c"
          if (!cpufeatures.exists) {
            sys.error("Could not find cpu-features.c required for the Android build")
          }
          cpufeatures
        case None =>
          sys.error("Could not find Android NDK (you may need to set the ANDROID_NDK_HOME env var)")
      }
    }
  )

  def jniSettings(target: Platform): Seq[Setting[_]] = {
    target match {
      case Android(platform) => androidSettings(platform)
      case Host              => Nil
    }
  }

}
