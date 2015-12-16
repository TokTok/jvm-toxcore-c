package im.tox.client.http

import org.scalatest.FunSuite

import scala.util.Random

final class TimeQueueTest extends FunSuite {

  val random = new Random()

  test("empty queue") {
    val queue = new TimeQueue(100)
    assert(queue.average == 0)
  }

  test("large queue") {
    val queue = new TimeQueue(6000)
    (0 until queue.maxSize).foreach(_ => queue.update())
    assert(queue.average == 0)
  }

  test("fast average implementation") {
    val queue = new TimeQueue(100)
    for (_ <- 0 until queue.maxSize) {
      Thread.sleep(random.nextInt(20))
      queue.update()
    }
    assert(queue.averageOpt == queue.averageRef)
  }

}
