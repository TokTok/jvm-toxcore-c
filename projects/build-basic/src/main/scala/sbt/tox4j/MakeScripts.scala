package sbt.tox4j

import sbt.Keys._
import sbt._

object MakeScripts extends OptionalPlugin {

  object Keys {
    val makeScripts = TaskKey[Unit]("makeScripts")
  }

  import Keys._

  private def classBaseName(s: String): String = {
    val lastDot = s.lastIndexOf('.')
    if (lastDot == -1) {
      s
    } else {
      s.substring(lastDot + 1)
    }
  }

  private def makeScriptsTask(base: File, opts: Seq[String], cp: Classpath, mains: Seq[String]) = {
    val template =
      """#!/usr/bin/env perl
        |my @CLASSPATH = (
        |  "%s"
        |);
        |exec "java",
        |  "%s",
        |  "-classpath", (join ":", @CLASSPATH),
        |  "%s", @ARGV
        |""".stripMargin
    for (main <- mains) {
      val contents = template.format(
        cp.files.get.mkString("\",\n  \""),
        opts.mkString("\",\n  \""),
        main
      )
      val out = base / "bin" / classBaseName(main)
      IO.write(out, contents)
      out.setExecutable(true)
    }
  }

  override val moduleSettings: Seq[Setting[_]] = {
    makeScripts <<= (
      baseDirectory,
      javaOptions in Test,
      fullClasspath in Test,
      discoveredMainClasses in Test
    ) map makeScriptsTask
  }

}
