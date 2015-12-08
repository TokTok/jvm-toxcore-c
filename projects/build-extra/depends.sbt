resolvers += Classpaths.sbtPluginReleases
resolvers += "Tox4j snapshots" at "https://tox4j.github.io/repositories/snapshots/"

// Import build-basic transitively.
addSbtPlugin("im.tox" % "build-basic" % "0.1-SNAPSHOT")

// Code style.
addSbtPlugin("org.brianmckenna" % "sbt-wartremover" % "0.14")

// Test coverage.
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.1.0")
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.0.0")

// Test dependencies.
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.0-M14"
) map (_ % Test)
