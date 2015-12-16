package im.tox.client.http

import scala.collection.mutable

final class TimeQueue(val maxSize: Int, queue: mutable.Queue[Long] = new mutable.Queue) {

  def update(): Unit = {
    queue.enqueue(System.currentTimeMillis())
    if (queue.length > maxSize) {
      queue.dequeue()
    }
  }

  /**
   * Reference implementation.
   */
  def averageRef: Int = {
    if (queue.isEmpty) {
      0
    } else {
      val times = queue.zip(queue.tail).map { case (prev, next) => (next - prev).toInt }
      times.sum / times.length
    }
  }

  /**
   * Optimised implementation.
   */
  def averageOpt: Int = {
    if (queue.isEmpty) {
      0
    } else {
      var sum = 0

      val a = queue.iterator
      val b = queue.iterator
      b.next()

      while (b.hasNext) {
        sum += (b.next() - a.next()).toInt
      }

      sum / (queue.length - 1)
    }
  }

  def average: Int = averageOpt

}
