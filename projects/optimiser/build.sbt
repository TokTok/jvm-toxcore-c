organization  := "im.tox"
name          := "optimiser"

sbtPlugin := true

// Build dependencies.
libraryDependencies ++= Seq(
  //"org.robovm" % "robovm-soot" % "2.5.0-2"
)

// Test dependencies.
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.0-M14"
) map (_ % Test)

// Enable the plugins we want.
import sbt.tox4j._
import sbt.tox4j.lint._
Checkstyle.moduleSettings
Scalastyle.moduleSettings
CodeFormat.moduleSettings
