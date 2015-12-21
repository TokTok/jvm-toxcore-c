organization  := "im.tox"
name          := "linters"
scalaVersion  := "2.11.7"

// Build dependencies.
libraryDependencies ++= Seq(
  "org.brianmckenna" %% "wartremover" % "0.14"
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
