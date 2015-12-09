// General settings.
organization  := "im.tox"
name          := "tox4j"
scalaVersion  := "2.11.7"

// Enable the plugins we want.
import sbt.tox4j._
import sbt.tox4j.lint._
import scoverage.ScoverageSbtPlugin.ScoverageKeys._

// Build plugins.
Assembly.moduleSettings
Benchmarking.projectSettings
CodeFormat.projectSettings
Jni.moduleSettings
ProtobufJni.moduleSettings


/******************************************************************************
 * Dependencies
 ******************************************************************************/


// Snapshot and linter repository.
resolvers += Resolver.sonatypeRepo("snapshots")

// Tox4j support libraries.
resolvers += "Tox4j snapshots" at "https://tox4j.github.io/repositories/snapshots/"

// Build dependencies.
libraryDependencies ++= Seq(
  "codes.reactive" %% "scala-time-threeten" % "0.3.0-SNAPSHOT",
  "com.google.guava" % "guava" % "18.0",
  "com.intellij" % "annotations" % "12.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "im.tox" %% "macros" % version.value,
  "org.scalaz" %% "scalaz-core" % "7.2.0-M1",
  "org.scalaz.stream" %% "scalaz-stream" % "0.8",
  "org.scodec" %% "scodec-core" % "1.8.3"
)

// Test dependencies.
libraryDependencies ++= Seq(
  "com.assembla.scala-incubator" %% "graph-core" % "1.9.4",
  "com.assembla.scala-incubator" %% "graph-dot" % "1.10.0",
  "com.intellij" % "forms_rt" % "7.0.3",
  "com.storm-enroute" %% "scalameter" % "0.8-SNAPSHOT",
  "junit" % "junit" % "4.12",
  "org.scalacheck" %% "scalacheck" % "1.12.5",
  "org.scalatest" %% "scalatest" % "3.0.0-M14",
  "org.slf4j" % "slf4j-log4j12" % "1.7.13"
) map (_ % Test)

// Add ScalaMeter as test framework.
testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

// JNI.
import sbt.tox4j.Jni.Keys._

packageDependencies ++= Seq(
  "protobuf",
  "libtoxcore",
  "libtoxav",
  // Required, since toxav's pkg-config files are incomplete:
  "libsodium",
  "vpx"
)

// TODO: infer this (easy).
jniSourceFiles in Compile ++= Seq(
  managedNativeSource.value / "Av.pb.cc",
  managedNativeSource.value / "Core.pb.cc",
  managedNativeSource.value / "ProtoLog.pb.cc"
)


/******************************************************************************
 * Other settings and plugin configuration.
 ******************************************************************************/


// Mixed project.
compileOrder := CompileOrder.Mixed
scalaSource in Compile := (javaSource in Compile).value
scalaSource in Test    := (javaSource in Test   ).value

// Lint plugins.
Checkstyle.moduleSettings
Findbugs.moduleSettings
Foursquare.moduleSettings
Scalastyle.moduleSettings

// Local overrides for linters.
WartRemoverOverrides.moduleSettings

// TODO(iphydf): Require less test coverage for now, until ToxAv is tested.
Coverage.projectSettings
coverageMinimum := 60

// Tox4j-specific style checkers.
addCompilerPlugin("im.tox" %% "linters" % "0.1-SNAPSHOT")

// Override Scalastyle configuration for test.
scalastyleConfigUrl in Test := None
scalastyleConfig in Test := (scalaSource in Test).value / "scalastyle-config.xml"

scalacOptions ++= Seq("-optimise", "-Yinline-warnings")


/******************************************************************************
 * Proguard configuration.
 ******************************************************************************/


/*
proguardSettings

ProguardKeys.proguardVersion in Proguard := "5.0"
ProguardKeys.inputs in Proguard := Seq((classDirectory in Compile).value)
ProguardKeys.options in Proguard ++= Seq(
  "-verbose",
  "-optimizationpasses 5",
  "-allowaccessmodification",
  "@" + (baseDirectory.value / "tools" / "proguard.txt").getPath
)
*/
