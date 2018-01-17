// Common tox4j build rules.
resolvers += Resolver.bintrayIvyRepo("toktok", "sbt-plugins")
addSbtPlugin("org.toktok" % "sbt-plugins" % "0.1.4")

// Compiler version for additional plugins in this project.
scalaVersion  := "2.10.7"

// Build dependencies.
libraryDependencies ++= Seq(
  "com.github.os72" % "protoc-jar" % "3.5.0"
)

addSbtPlugin("com.trueaccord.scalapb" % "sbt-scalapb" % "0.5.43")
