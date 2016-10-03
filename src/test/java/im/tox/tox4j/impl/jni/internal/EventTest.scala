package im.tox.tox4j.impl.jni.internal

import org.scalatest.FlatSpec

@SuppressWarnings(Array("org.wartremover.warts.Equals"))
class EventTest extends FlatSpec {

  "callback" should "be called on run()" in {
    val event = new Event
    var called = false

    val id = event += (() => called = true)

    event()
    assert(called)
  }

  it should "be called twice if run() is called twice" in {
    val event = new Event
    var called = 0

    val id = event += (() => called += 1)

    event()
    assert(called == 1)
    event()
    assert(called == 2)
  }

  it should "not be called if it was deleted" in {
    val event = new Event
    var called = false

    val id = event += (() => called = true)

    event -= id

    event()
    assert(!called)
  }

  "remove" should "be idempotent" in {
    val event = new Event
    var called = 0

    val id1 = event += (() => called = 1)
    val id2 = event += (() => called = 2)

    event -= id1
    event -= id1

    event()
    assert(called == 2)
  }

}
