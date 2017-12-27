// General settings.
organization  := "org.toktok"
name          := "tox4j-c"
version       := "0.1.3-SNAPSHOT"
scalaVersion  := "2.11.12"

bintrayVcsUrl := Some("https://github.com/TokTok/jvm-toxcore-c")

/******************************************************************************
 * Dependencies
 ******************************************************************************/

// Snapshot and linter repository.
resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += Resolver.bintrayRepo("toktok", "maven")

// Build dependencies.
libraryDependencies ++= Seq(
  "org.toktok" %% "tox4j-api" % "0.1.3",
  "org.toktok" %% "macros" % "0.1.0",
  "com.trueaccord.scalapb" %% "scalapb-runtime-grpc" % "0.5.46"
)

// Test dependencies.
libraryDependencies ++= Seq(
  "com.intellij" % "forms_rt" % "7.0.3",
  "com.storm-enroute" %% "scalameter" % "0.7",
  "jline" % "jline" % "2.14.2",
  "junit" % "junit" % "4.12",
  "org.scalacheck" %% "scalacheck" % "1.13.4",
  "org.scalatest" %% "scalatest" % "3.0.1",
  "org.scalaz" %% "scalaz-concurrent" % "7.2.8",
  "org.slf4j" % "slf4j-log4j12" % "1.7.22"
) map (_ % Test)

// Add ScalaMeter as test framework.
testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

// Disable parallel test execution, as network tests become flaky that way.
parallelExecution in Test := false

/******************************************************************************
 * Other settings and plugin configuration.
 ******************************************************************************/

// TODO(iphydf): Require less test coverage for now, until ToxAv is tested.
import scoverage.ScoverageKeys._
coverageMinimum := 20
coverageExcludedPackages := ".*\\.proto\\..*"

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
