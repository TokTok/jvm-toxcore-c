organization  := "im.tox"
name          := "build-basic"

sbtPlugin := true

// https://github.com/scoverage/sbt-scoverage#highlighting
ScoverageSbtPlugin.ScoverageKeys.coverageHighlighting := false
ScoverageSbtPlugin.ScoverageKeys.coverageMinimum := 0

// Enable the plugins we want. Here we also need to explicitly apply AutoPlugins,
// because they are not loaded correctly by SBT in our bootstrap.
import sbt.tox4j._
import sbt.tox4j.lint._
Scalastyle.moduleSettings
Benchmarking.moduleSettings
CodeFormat.moduleSettings
