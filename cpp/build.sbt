// General settings.
organization  := "org.toktok"
name          := "tox4j-c_" + sys.env("TOX4J_PLATFORM")
version       := ("pkg-config --modversion toxcore" !!).trim

// Pure Java project.
crossPaths := false
autoScalaLibrary := false

// Bintray publishing settings.
licenses += (("AGPL-V3", url("http://opensource.org/licenses/AGPL-V3")))
bintrayOrganization := Some("toktok")
bintrayVcsUrl := Some("https://github.com/TokTok/jvm-toxcore-c")
