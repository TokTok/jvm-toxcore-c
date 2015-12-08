package im.tox.optimiser

import java.io.File
import java.util

import soot._
import soot.options.Options

object ByteCodeOptimiser {

  PackManager.v.getPack("jop").add(new Transform("jop.iphy-ule", new UnusedLocalRemover))

  def process(classpath: Seq[File], inputPath: File, outputPath: File): scala.Unit = {
    val fullClasspath = Seq(
      new File(sys.props("java.home"), "lib/rt.jar"),
      new File(sys.props("java.home"), "/lib/jce.jar")
    ) ++ classpath
    Options.v.set_soot_classpath(fullClasspath.mkString(":"))
    Options.v.set_output_format(Options.output_format_class)
    Options.v.set_process_dir(util.Arrays.asList(inputPath.getPath))
    Options.v.set_output_dir(outputPath.getPath)

    PhaseOptions.v.setPhaseOption("jop", "on")
    PhaseOptions.v.setPhaseOption("jop.ule", "off")
    PhaseOptions.v.setPhaseOption("jop.iphy-ule", "on")

    Main.v.run(Array("-optimize"))
  }

}
