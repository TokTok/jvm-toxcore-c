package im.tox.core.error

import im.tox.core.error.CoreError.CoreException
import scodec.Err.General
import scodec.{Attempt, Err}

import scalaz.{-\/, \/, \/-}

sealed abstract class CoreError {
  val exception = new CoreException(this)
}

object CoreError {

  final case class CoreException(error: CoreError) extends Exception(error.toString)

  final case class Unimplemented(message: String) extends CoreError
  final case class ValidationError(message: String) extends CoreError
  final case class InvalidFormat(message: String) extends CoreError
  final case class CodecError(cause: Err) extends CoreError
  final case object DecryptionError extends CoreError

  def apply[A](option: Option[A], message: String): CoreError \/ A = {
    option match {
      case None        => -\/(ValidationError(message))
      case Some(value) => \/-(value)
    }
  }

  def apply[A](attempt: Attempt[A]): CoreError \/ A = {
    attempt match {
      case Attempt.Failure(cause)    => -\/(CodecError(cause))
      case Attempt.Successful(value) => \/-(value)
    }
  }

  def toAttempt[A](value: CoreError \/ A): Attempt[A] = {
    Attempt.fromEither(value.leftMap {
      case CodecError(cause) => cause
      case error             => new General(error.toString)
    }.toEither)
  }

  def require(condition: Boolean, message: => String): CoreError \/ Unit = {
    if (condition) {
      \/-(())
    } else {
      -\/(ValidationError(message))
    }
  }

}
