resolvers += Classpaths.sbtPluginReleases
resolvers += "Tox4j snapshots" at "https://tox4j.github.io/repositories/snapshots/"
resolvers += "sonatype-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

// Import build-basic transitively.
addSbtPlugin("im.tox" % "build-basic" % "0.1-SNAPSHOT")

// Code style.
addSbtPlugin("org.brianmckenna" % "sbt-wartremover" % "0.13")

// Test coverage.
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.1.0")
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.0.0.BETA1")

// Test dependencies.
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4"
) map (_ % Test)
