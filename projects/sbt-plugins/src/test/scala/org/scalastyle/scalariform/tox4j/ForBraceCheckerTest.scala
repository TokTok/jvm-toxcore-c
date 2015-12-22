package org.scalastyle.scalariform.tox4j

import org.scalastyle.scalariform.CheckerTest

final class ForBraceCheckerTest extends CheckerTest(new ForBraceChecker) {

  test("flag braces on the same line") {
    assertErrors(1, """
class Foo {
  for !!{ t <- List(1,2,3) } yield t
}""")
  }

  test("do not flag parentheses on the same line") {
    assertErrors(0, """
class Foo {
  for (t <- List(1,2,3)) yield t
}""")
  }

  test("flag parentheses on a separate line") {
    assertErrors(1, """
class Foo {
  for !!(
    t <- List(1,2,3)
  ) yield t
}""")
  }

  test("do not flag braces on a separate line") {
    assertErrors(0, """
class Foo {
  for {
    t <- List(1,2,3)
  } yield t
}""")
  }

}
