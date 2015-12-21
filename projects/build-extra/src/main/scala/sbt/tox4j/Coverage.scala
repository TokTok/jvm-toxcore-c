package sbt.tox4j

import scoverage.ScoverageSbtPlugin.ScoverageKeys._

/**
 * Default settings object for coverage. Requires 100% coverage on everything except generated protobuf code.
 */
object Coverage extends OptionalPlugin {

  object Keys

  /**
   * Require 100% coverage. Fail the test if coverage is not met.
   */
  override def moduleSettings: Seq[sbt.Setting[_]] = Seq(
    // Require 100% test coverage.
    coverageMinimum := 100,
    coverageFailOnMinimum := true,

    // Ignore generated proto sources in coverage.
    coverageExcludedPackages := ".*\\.proto\\..*"
  )

}
