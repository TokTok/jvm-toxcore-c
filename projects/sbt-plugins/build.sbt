organization  := "im.tox"
name          := "sbt-plugins"
scalaVersion  := "2.10.6"

sbtPlugin := true

resolvers += Classpaths.sbtPluginReleases

// Code style.
addSbtPlugin("com.etsy" % "sbt-checkstyle-plugin" % "0.5.3")
addSbtPlugin("org.scalastyle" % "scalastyle-sbt-plugin" % "0.8.0")
addSbtPlugin("org.brianmckenna" % "sbt-wartremover" % "0.14")

// Code formatting.
addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

// Scala protobuf support.
addSbtPlugin("com.trueaccord.scalapb" % "sbt-scalapb" % "0.5.21")

// Test coverage.
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.1.0")
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.0.0")

// Proguard.
addSbtPlugin("com.typesafe.sbt" % "sbt-proguard" % "0.2.2")

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
