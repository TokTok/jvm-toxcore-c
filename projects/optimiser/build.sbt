organization  := "im.tox"
name          := "optimiser"
scalaVersion  := "2.11.7"

// Build dependencies.
libraryDependencies ++= Seq(
  //"org.robovm" % "robovm-soot" % "2.5.0-2"
)

// Test dependencies.
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4"
) map (_ % Test)

// Enable the plugins we want.
sbt.tox4j.lint.Checkstyle.moduleSettings
sbt.tox4j.lint.Scalastyle.moduleSettings
sbt.tox4j.CodeFormat.projectSettings
