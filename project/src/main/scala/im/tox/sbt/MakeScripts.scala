package im.tox.sbt

import sbt.Keys._
import sbt._

@SuppressWarnings(Array(
  "org.wartremover.warts.Equals"
))
object MakeScripts extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  object Keys {
    val makeScripts: TaskKey[Unit] = TaskKey[Unit]("makeScripts")
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

  private def makeScriptsTask(base: File, cp: Classpath, mains: Seq[String]) = {
    val template =
      """#!/usr/bin/env perl
        |my @CLASSPATH = (
        |  "%s"
        |);
        |exec "java",
        |  "-classpath", (join ":", @CLASSPATH),
        |  "%s", @ARGV
        |""".stripMargin
    for (main <- mains) {
      val contents = template.format(
        cp.files.get.mkString("\",\n  \""),
        main
      )
      val out = base / "bin" / classBaseName(main)
      IO.write(out, contents)
      out.setExecutable(true)
    }
  }

  override val projectSettings: Seq[Setting[_]] = {
    makeScripts := makeScriptsTask(
      baseDirectory.value,
      (fullClasspath in Test).value,
      (discoveredMainClasses in Test).value
    )
  }

}
