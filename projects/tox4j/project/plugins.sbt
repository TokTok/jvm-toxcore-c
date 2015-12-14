resolvers += "Tox4j snapshots" at "https://tox4j.github.io/repositories/snapshots/"

addSbtPlugin("im.tox" % "build-extra" % "0.1-SNAPSHOT")
// addSbtPlugin("im.tox" % "optimiser" % "0.1-SNAPSHOT")

addSbtPlugin("com.typesafe.sbt" % "sbt-proguard" % "0.2.2")
