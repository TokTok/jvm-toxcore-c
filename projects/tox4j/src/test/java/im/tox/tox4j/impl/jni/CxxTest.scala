package im.tox.tox4j.impl.jni

import java.io.File

import org.scalatest.FunSuite

import scala.util.Try
import im.tox.tox4j.OptimisedIdOps._

final class CxxTest extends FunSuite {

  test("C++ unit tests (gtest)") {
    val directory = new File("target/cpp/_build")
    assert(directory.exists)
    assert(directory.isDirectory)

    val success = Seq("ninja", "make").exists { buildTool =>
      Try(
        (new ProcessBuilder(buildTool, "test")
          |> (_.directory(directory))
          |> { process =>
            process.environment.put("CTEST_OUTPUT_ON_FAILURE", "1")
            process
          }
          |> (_.inheritIO())
          |> (_.start())
          |> (_.waitFor())) == 0
      ).getOrElse(false)
    }

    assert(success, ", one or more C++ tests failed; see messages above")
  }

}
