organization  := "im.tox"
name          := "macros"
scalaVersion  := "2.11.7"

// Build dependencies.
libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-compiler" % scalaVersion.value
)

// Test dependencies.
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4"
) map (_ % Test)

// TODO(iphydf): scoverage doesn't instrument the macro implementation, causing
// 0% coverage. In fact, we have 100% coverage except for the @compileTimeOnly
// implicit conversion.
ScoverageSbtPlugin.ScoverageKeys.coverageMinimum := 0

// Enable the plugins we want.
sbt.tox4j.lint.Checkstyle.moduleSettings
sbt.tox4j.lint.Scalastyle.moduleSettings
sbt.tox4j.CodeFormat.projectSettings
