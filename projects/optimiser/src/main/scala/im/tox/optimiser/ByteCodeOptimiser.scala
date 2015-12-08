package im.tox.optimiser

import soot._
import soot.options.Options
import soot.toolkits.scalar.UnusedLocalEliminator

object ByteCodeOptimiser {

  def process(): scala.Unit = {
    val classpath = Seq(
      sys.props("java.home") + "/lib/rt.jar",
      sys.props("java.home") + "/lib/jce.jar",
      sys.env("HOME") + "/.ivy2/cache/org.scala-lang/scala-library/jars/scala-library-2.11.7.jar",
      sys.env("HOME") + "/.ivy2/cache/org.scalatest/scalatest_2.11/jars/scalatest_2.11-2.2.4.jar",
      "target/scala-2.11/classes",
      "target/scala-2.11/test-classes"
    )
    Options.v.set_soot_classpath(classpath.mkString(":"))
    Options.v.set_output_format(Options.output_format_jimple)

    PackManager.v.getPack("jop").add(new Transform("jop.iphy-ule", new UnusedLocalRemover))

    Main.v.run(Array(
      "-p", "jop", "on",
      "-p", "jop.ule", "off",
      "-p", "jop.iphy-ule", "on",
      "-process-dir", "target/scala-2.11/test-classes"
    ))
  }

}
