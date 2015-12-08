package im.tox.tox4j.testing

/**
 * Wrapper for an [[Ordering]] that counts the number of comparisons made.
 */
final case class CountingOrdering[A](ord: Ordering[A]) extends Ordering[A] {

  var count = 0

  override def compare(x: A, y: A): Int = {
    count += 1
    ord.compare(x, y)
  }

}
