// Common tox4j build rules.
addSbtPlugin("org.toktok" % "sbt-plugins" % "0.1.6")

// Compiler version for additional plugins in this project.
scalaVersion  := "2.10.7"

// Build dependencies.
libraryDependencies ++= Seq(
  "com.github.os72" % "protoc-jar" % "3.4.0"
)

addSbtPlugin("com.trueaccord.scalapb" % "sbt-scalapb" % "0.5.43")
