package im.tox.tox4j

import im.tox.tox4j.OptimisedIdOps._
import org.scalatest.FunSuite

final class OptimisedIdOpsTest extends FunSuite {

  private final class Foo {
    def a[A <: Int](int: A): Int = int + 1
    def b(int: Int): Int = int + 2
    def c(int: Int): Int = int + 3

    def test(): Unit = {
      val piped = 1 |> a |> b |> c
      val called = c(b(a[Int](1)))
      assert(piped == called)
    }
  }

  test("|>") {
    new Foo().test()
  }

  test("lambdas") {
    val x = 1 |> ((x: Int) => x + 1)
    assert(x == 2)
  }

  test("lambdas with same name as outer name") {
    val x = 1
    val y = x |> { (x: Int) =>
      assert(x == 1)
      x + 1
    }
    assert(y == 2)
  }

  test("named lambdas") {
    val lambda = (x: Int) => x + 1
    val x = 1 |> lambda
    assert(x == 2)
  }

}
