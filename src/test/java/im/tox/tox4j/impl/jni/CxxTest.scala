package im.tox.tox4j.impl.jni

import java.io.File

import im.tox.tox4j.OptimisedIdOps._
import org.scalatest.FunSuite

@SuppressWarnings(Array("org.wartremover.warts.Equals"))
final class CxxTest extends FunSuite {

  def osName: String = {
    sys.props("os.name") match {
      case "Linux" => "linux"
      case "Mac OS X" => "darwin"
    }
  }

  def archName: String = {
    sys.props("os.arch") match {
      case "amd64" | "x86_64" => "x86_64"
    }
  }

  def signalName: Map[Int, String] = Map(
    1 -> "Hangup",
    2 -> "Interrupted",
    3 -> "Quit program",
    4 -> "Illegal instruction",
    5 -> "Trace trap",
    6 -> "Aborted",
    7 -> "Emulate instruction executed",
    8 -> "Floating point exception (likely division by zero)",
    9 -> "Killed",
    10 -> "Bus error",
    11 -> "Segmentation fault",
    12 -> "Non-existent system call invoked",
    13 -> "Write on a pipe with no reader",
    14 -> "Real-time timer expired",
    15 -> "Software termination signal",
    24 -> "CPU time limit exceeded",
    25 -> "File size limit exceeded",
    26 -> "Virtual time alarm",
    27 -> "Profiling timer alarm",
    30 -> "User defined signal 1",
    31 -> "User defined signal 2"
  )

  def errorMessage(result: Int): String = {
    result & 127 match {
      case 0 =>
        s"One or more C++ tests failed; see messages above (exit code: $result)"
      case n =>
        s"Process terminated with signal $n: ${signalName(n)}"
    }
  }

  test("C++ unit tests (gtest)") {
    val testProgram = new File(s"target/$archName-$osName/tox4j-c_test")
    assert(testProgram.exists, s"; test program '$testProgram' does not exist")

    val result = (new ProcessBuilder(testProgram.getPath)
      |> (_.inheritIO())
      |> (_.start())
      |> (_.waitFor()))

    if (result != 0) {
      fail(errorMessage(result))
    }
  }

}
