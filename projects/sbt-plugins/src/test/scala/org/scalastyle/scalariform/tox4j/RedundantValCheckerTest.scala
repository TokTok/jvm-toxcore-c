package org.scalastyle.scalariform.tox4j

import org.scalastyle.scalariform.CheckerTest

final class RedundantValCheckerTest extends CheckerTest(new RedundantValChecker) {

  test("flag redundant val in case classes") {
    assertErrors(1, "case class Foo(!!val thing: Int)")
  }

  test("flag redundant val in final case classes") {
    assertErrors(1, "final case class Foo(!!val thing: Int)")
  }

  test("do not flag var in case classes") {
    assertErrors(0, "case class Foo(var thing: Int)")
  }

  test("do not flag case classes without val") {
    assertErrors(0, "case class Foo(thing: Int)")
    assertErrors(0, "final case class Foo(thing: Int)")
    assertErrors(0, "abstract case class Foo(thing: Int)")
  }

  test("do not flag normal classes with val inside case classes") {
    assertErrors(0, """
case class Foo(thing: Int) {
  class Bar(val otherThing: Int)
}""")
  }

  test("do not flag val in non-case classes") {
    assertErrors(0, """
class Foo(val thing: Int)
abstract class Foo(val thing: Int)
final class Foo(val thing: Int)""")
  }

}
