package sbt.tox4j

import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}

import sbt.Keys._
import sbt._

// scalastyle:off
object Benchmarking extends OptionalPlugin {

  val Benchmark = config("benchmark")

  object Keys {
    val machine = settingKey[String]("Name of the machine running the benchmark.")

    val execute = taskKey[Unit]("Run all benchmarks.")
    val upload = taskKey[Unit]("Upload bench mark results.")
    val benchmark = taskKey[Unit]("Run all benchmarks and upload the results.")
  }

  import Keys._

  override val moduleSettings = inConfig(Benchmark)(Seq(
    machine := "travis",

    execute := (testOnly in Test).toTask(" *Bench").value,

    upload := {
      uploadResults(streams.value.log, baseDirectory.value, machine.value, target.value)
    },

    benchmark := {
      val () = execute.value
      uploadResults(streams.value.log, baseDirectory.value, machine.value, target.value)
    }

  )) ++ Seq(
    test in Test := (testOnly in Test).toTask(" *Test").value
  )

  private def uploadResults(log: Logger, baseDirectory: File, machine: String, target: File) = {
    val webDir = baseDirectory / ".web" / "report" / machine

    def dataJs(dir: File) = {
      dir / "js" / "ScalaMeter" / "data.js"
    }

    def git(args: String*) = {
      Process("git" +: args, webDir) ! log
    }

    if (webDir.exists) {
      git("config", "push.default", "simple")

      git("pull")
      IO.copyFile(dataJs(target / "benchmarks" / "report"), dataJs(webDir))
      git("commit", "-a", "-m", s"Added benchmark results of $machine at $now")
      git("push")
    }
  }

  private def now: String = {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm")
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
    dateFormat.format(new Date)
  }

}
