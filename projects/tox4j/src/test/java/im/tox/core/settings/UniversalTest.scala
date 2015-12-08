package im.tox.core.settings

import org.scalatest.FunSuite

final class UniversalTest extends FunSuite {

  test("inject Int, project Int") {
    val injected = Universal.uniInt.inject(3)
    assert(Universal.uniInt.project(injected).contains(3))
  }

  test("inject Int, project String") {
    val injected = Universal.uniInt.inject(3)
    assert(Universal.uniString.project(injected).isEmpty)
  }

  test("inject Int, project Int with new Universal") {
    val injected = Universal.uniInt.inject(3)

    val newUniInt = Universal.embed[Int]
    assert(newUniInt.project(injected).contains(3))
  }

}
