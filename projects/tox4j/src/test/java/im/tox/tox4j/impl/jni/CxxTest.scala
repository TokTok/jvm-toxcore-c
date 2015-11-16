package im.tox.tox4j.impl.jni

import java.io.File

import org.scalatest.FunSuite

import scala.util.Try

final class CxxTest extends FunSuite {

  test("C++ unit tests (gtest)") {
    val directory = new File("target/cpp/_build")
    assert(directory.exists)
    assert(directory.isDirectory)

    val success = Seq("ninja", "make").exists { buildTool =>
      Try(
        new ProcessBuilder(buildTool, "test")
          .directory(directory)
          .inheritIO()
          .start()
          .waitFor() == 0
      ).getOrElse(false)
    }

    assert(success, "One or more C++ tests failed; see messages above")
  }

}
