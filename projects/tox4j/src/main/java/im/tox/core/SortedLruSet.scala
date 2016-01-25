package im.tox.core

import com.github.nscala_time.time.Imports._

import scala.annotation.tailrec
import scala.collection.immutable.SortedMap

/**
 * Ordered set of elements associated with an insertion time.
 *
 * Allows for two types of truncation:
 * - removal of the largest elements on add/update
 * - removal of the oldest elements (LRU stands for Least Recently Used)
 *
 * The largest elements are selected according to a given [[Ordering]], the
 * oldest are filtered out according to a given time horizon.
 */
final case class SortedLruSet[T] private (private val underlying: SortedMap[T, DateTime]) extends AnyVal {

  /**
   * Remove the largest elements from the set until the size is less than or
   * equal to capacity.
   *
   * @param capacity Maximum size of the set.
   */
  @tailrec
  private def truncate(capacity: Int)(implicit ord: Ordering[T]): SortedLruSet[T] = {
    assert(capacity >= 0)

    if (capacity >= size) {
      this
    } else {
      SortedLruSet(underlying - underlying.max._1).truncate(capacity)
    }
  }

  /**
   * Remove elements that are older than a given time horizon.
   *
   * @param timeHorizon [[DateTime]] of the oldest time allowed.
   */
  def removeStale(timeHorizon: DateTime)(implicit ord: Ordering[T]): SortedLruSet[T] = {
    // Get the nodes that are older than timeHorizon.
    val result = underlying.filter {
      case (_, lastActive) =>
        lastActive < timeHorizon
    }.foldLeft(underlying) { (nodes, expiredNode) =>
      nodes - expiredNode._1
    }
    SortedLruSet(result)
  }

  def +(element: T, modificationTime: DateTime, capacity: Int)(implicit ord: Ordering[T]): SortedLruSet[T] = { // scalastyle:ignore method.name
    SortedLruSet(underlying.updated(element, modificationTime)).truncate(capacity)
  }

  def ++(nodes: SortedLruSet[T], capacity: Int)(implicit ord: Ordering[T]): SortedLruSet[T] = { // scalastyle:ignore method.name
    SortedLruSet(underlying ++ nodes.underlying).truncate(capacity)
  }

  def -(element: T): SortedLruSet[T] = SortedLruSet(underlying - element) // scalastyle:ignore method.name

  def contains(element: T): Boolean = underlying.contains(element)

  def exists(predicate: T => Boolean): Boolean = {
    underlying.exists {
      case (element, _) =>
        predicate(element)
    }
  }

  def find(predicate: T => Boolean): Option[T] = {
    underlying.find {
      case (element, _) =>
        predicate(element)
    }.map(_._1)
  }

  def isEmpty: Boolean = underlying.isEmpty
  def nonEmpty: Boolean = underlying.nonEmpty

  def size: Int = underlying.size

  def keySet: Set[T] = underlying.keySet
  def iterator: Iterator[(T, DateTime)] = underlying.iterator

}

object SortedLruSet {

  def empty[T](implicit ord: Ordering[T]): SortedLruSet[T] = SortedLruSet(SortedMap.empty)

}
