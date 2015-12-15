package im.tox.client.http

import scala.collection.mutable

final class TimeQueue(val maxSize: Int, queue: mutable.Queue[Long] = new mutable.Queue) {

  def update(): Unit = {
    queue.enqueue(System.currentTimeMillis())
    if (queue.length > maxSize) {
      queue.dequeue()
    }
  }

  def average: Int = {
    val times = queue.zip(queue.tail).map { case (prev, next) => (next - prev).toInt }
    times.sum / times.length
  }

}
