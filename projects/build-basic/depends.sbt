/**
 * The build configuration in this project is split up into dependencies and
 * build configuration. We do this so that it can be shared between the SBT
 * build and the project build itself. This way, this project can benefit from
 * the checkers and formatters provided by itself, recursively.
 */

resolvers += Classpaths.sbtPluginReleases
resolvers += "Tox4j snapshots" at "https://tox4j.github.io/repositories/snapshots/"

// Code style.
addSbtPlugin("com.etsy" % "sbt-checkstyle-plugin" % "0.5.3")
addSbtPlugin("org.scalastyle" % "scalastyle-sbt-plugin" % "0.8.0")
addSbtPlugin("de.johoop" % "findbugs4sbt" % "1.4.0")

// Code formatting.
addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

// Scala protobuf support.
addSbtPlugin("com.trueaccord.scalapb" % "sbt-scalapb" % "0.5.15")

// Build dependencies.
libraryDependencies ++= Seq(
  "com.github.os72" % "protoc-jar" % "3.0.0-b1",
  "commons-io" % "commons-io" % "2.4",
  "org.ow2.asm" % "asm-all" % "5.0.2",
  "javassist" % "javassist" % "3.12.1.GA"
)

// Test dependencies.
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.0-M14"
) map (_ % Test)
