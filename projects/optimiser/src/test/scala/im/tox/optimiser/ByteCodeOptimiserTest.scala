package im.tox.optimiser

import java.io.File

import org.scalatest.FunSuite

final class ByteCodeOptimiserTest extends FunSuite {

  test("jimple") {
    ByteCodeOptimiser.process(
      Seq(
        new File(sys.env("HOME") + "/.ivy2/cache/org.scala-lang/scala-library/jars/scala-library-2.10.6.jar"),
        new File(sys.env("HOME") + "/.ivy2/cache/org.scalatest/scalatest_2.10/jars/scalatest_2.10-3.0.0-M14.jar"),
        new File(sys.env("HOME") + "/.ivy2/cache/org.scalactic/scalactic_2.10/jars/scalactic_2.10-3.0.0-M14.jar"),
        new File("target/scala-2.10/sbt-0.13/classes"),
        new File("target/scala-2.10/sbt-0.13/test-classes")
      ),
      new File("target/scala-2.10/sbt-0.13/test-classes"),
      new File("sootOutput")
    )
  }

}
