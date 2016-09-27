// General settings.
organization  := "im.tox"
name          := "optimiser"
scalaVersion  := "2.11.7"

sbtPlugin := true

// Build dependencies.
libraryDependencies ++= Seq(
  //"org.robovm" % "robovm-soot" % "2.5.0-2"
)

// Test dependencies.
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.0-M14"
) map (_ % Test)
