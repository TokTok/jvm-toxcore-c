import scoverage.ScoverageSbtPlugin.ScoverageKeys._
import com.typesafe.sbt.SbtScalariform.{ScalariformKeys, scalariformSettings}
import scalariform.formatter.preferences._

val commonSettings = scalariformSettings ++ Seq(
  // Require 100% test coverage.
  coverageMinimum := 100,
  coverageFailOnMinimum := true,

  // Ignore generated proto sources in coverage.
  coverageExcludedPackages := ".*\\.proto\\..*",

  ScalariformKeys.preferences := ScalariformKeys.preferences.value
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(DoubleIndentClassDeclaration, true)
)

lazy val `sbt-plugins`  = project.settings(commonSettings: _*).settings(coverageHighlighting := false)
lazy val macros         = project.settings(commonSettings: _*)
lazy val linters        = project.settings(commonSettings: _*)
lazy val tox4j          = project.settings(commonSettings: _*).dependsOn(macros)

im.tox.sbt.lint.Scalastyle.projectSettings
