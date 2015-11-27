package im.tox.tox4j.testing

import im.tox.core.error.CoreError
import org.scalatest.Assertions

import scala.language.implicitConversions
import scalaz.{-\/, \/, \/-}

final case class GetDisjunction[T] private (disjunction: CoreError \/ T) extends Assertions {
  def get: T = {
    disjunction match {
      case -\/(error)   => fail(error.toString)
      case \/-(success) => success
    }
  }
}

object GetDisjunction {

  implicit def toGetDisjunction[T](disjunction: CoreError \/ T): GetDisjunction[T] = GetDisjunction(disjunction)

}
