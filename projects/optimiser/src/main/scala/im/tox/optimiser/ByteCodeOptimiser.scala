package im.tox.optimiser

import soot.Main
import soot.options.Options

object ByteCodeOptimiser {

  def process(): Unit = {
    val classpath = Seq(
      sys.props("java.home") + "/lib/rt.jar",
      sys.props("java.home") + "/lib/jce.jar",
      sys.env("HOME") + "/.ivy2/cache/org.scala-lang/scala-library/jars/scala-library-2.11.7.jar",
      "target/scala-2.11/test-classes"
    )
    Options.v.set_soot_classpath(classpath.mkString(":"))
    Options.v.set_output_format(Options.output_format_jimple)
    Options.v.set_main_class("im.tox.optimiser.TestClass")

    Main.v.run(Array(
      "-whole-optimize",
      "-p", "wjop.smb", "on",
      "-p", "wjop.si", "on",
      "im.tox.optimiser.TestClass",
      "im.tox.optimiser.TestClass$"
    ))
  }

}
