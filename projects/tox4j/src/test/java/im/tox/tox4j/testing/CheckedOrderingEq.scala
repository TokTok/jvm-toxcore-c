package im.tox.tox4j.testing

import org.scalatest.Assertions

/**
 * Wrap an [[Ordering]] in another [[Ordering]] that checks whether !(a < b) && !(b < a) => a eq b.
 */
object CheckedOrderingEq extends Assertions {

  def apply[A <: AnyRef](ord: Ordering[A]): Ordering[A] = {
    new Ordering[A] {
      override def compare(x: A, y: A): Int = {
        val result = ord.compare(x, y)
        if (result == 0) {
          assert(x eq y)
        }
        result
      }
    }
  }

}
