// General settings.
organization  := "im.tox"
name          := "tox4j"
scalaVersion  := "2.11.7"


/******************************************************************************
 * Dependencies
 ******************************************************************************/


// Snapshot and linter repository.
resolvers += Resolver.sonatypeRepo("snapshots")

// Build dependencies.
libraryDependencies ++= Seq(
  "codes.reactive" %% "scala-time-threeten" % "0.3.0-SNAPSHOT",
  "com.google.guava" % "guava" % "18.0",
  "com.intellij" % "annotations" % "12.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "org.scalaz" %% "scalaz-core" % "7.2.0-M1",
  "org.scalaz.stream" %% "scalaz-stream" % "0.8",
  "org.scodec" %% "scodec-core" % "1.8.3"
)

// Test dependencies.
libraryDependencies ++= Seq(
  "com.assembla.scala-incubator" %% "graph-core" % "1.9.4",
  "com.assembla.scala-incubator" %% "graph-dot" % "1.10.0",
  "com.github.scopt" %% "scopt" % "3.3.0",
  "com.intellij" % "forms_rt" % "7.0.3",
  "com.storm-enroute" %% "scalameter" % "0.8-SNAPSHOT",
  "jline" % "jline" % "2.13",
  "junit" % "junit" % "4.12",
  "org.scalacheck" %% "scalacheck" % "1.12.5",
  "org.scalatest" %% "scalatest" % "3.0.0-M14",
  "org.slf4j" % "slf4j-log4j12" % "1.7.13"
) map (_ % Test)

// Add ScalaMeter as test framework.
testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

// Native dependencies.
import im.tox.sbt.NativeCompilePlugin.Keys.nativeLink
inConfig(Test)(Keys.compile <<= Keys.compile.dependsOn(nativeLink in NativeTest))

nativeLibraryDependencies ++= Seq(
  "protobuf" % "protobuf" % "3.0.0-beta-1",
  "libtoxcore" % "libtoxcore" % "0.0.0",
  "libtoxav" % "libtoxav" % "0.0.0",
  // Required, since toxav's pkg-config files are incomplete:
  "libsodium" % "libsodium" % "1.0.7",
  "vpx" % "vpx" % "1.5.0"
)


/******************************************************************************
 * Other settings and plugin configuration.
 ******************************************************************************/


// TODO(iphydf): Require less test coverage for now, until ToxAv is tested.
import scoverage.ScoverageSbtPlugin.ScoverageKeys._
coverageMinimum := 70

import im.tox.sbt.lint.Scalastyle
Scalastyle.projectSettings

// Override Scalastyle configuration for test.
scalastyleConfigUrl in Test := None
scalastyleConfig in Test := (scalaSource in Test).value / "scalastyle-config.xml"

// Mixed project.
compileOrder := CompileOrder.Mixed
scalaSource in Compile := (javaSource in Compile).value
scalaSource in Test    := (javaSource in Test   ).value


/******************************************************************************
 * Proguard configuration.
 ******************************************************************************/


proguardSettings

javaOptions in (Proguard, ProguardKeys.proguard) := Seq("-Xmx1g")
ProguardKeys.proguardVersion in Proguard := "5.1"
ProguardKeys.inputs in Proguard := (fullClasspath in Test).value.files.filterNot(f => Seq(
  "asm-5.0.4.jar",
  "jansi-1.11.jar",
  "scala-compiler-2.11.7.jar",
  "test-interface-1.0.jar"
).contains(f.getName))
ProguardKeys.binaryDeps in Proguard := (sbt.Keys.compile in Test).value.relations.allBinaryDeps.toSeq
ProguardKeys.options in Proguard += "@" + (baseDirectory.value / "tools" / "proguard.txt").getPath
