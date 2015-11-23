package im.tox.core

import scalaz.{-\/, \/, \/-}

/**
 * A number of utilities for functional programming.
 */
object Functional {

  /**
   * Fold a list of A \/ B into an A \/ Seq[B]. This favours A, meaning if any
   * element of the list is [[-\/]], the entire result is,
   *
   * This can be used to process a list of elements from an error disjunction
   * where any failed element should fail the entire list.
   */
  def foldDisjunctionList[A, B](list: Seq[A \/ B]): A \/ Seq[B] = {
    list.foldLeft(\/-(Nil): A \/ Seq[B]) {
      (list, element) =>
        for {
          list <- list
          element <- element
        } yield {
          element +: list
        }
    }
  }

}
