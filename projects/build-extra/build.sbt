organization  := "im.tox"
name          := "build-extra"

sbtPlugin := true

// https://github.com/scoverage/sbt-scoverage#highlighting
ScoverageSbtPlugin.ScoverageKeys.coverageHighlighting := false
ScoverageSbtPlugin.ScoverageKeys.coverageMinimum := 0

// Enable the plugins we want.
import sbt.tox4j.lint._
Checkstyle.moduleSettings
Scalastyle.moduleSettings
