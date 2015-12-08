package im.tox.core.settings

import im.tox.core.settings.Universal.Value

import scala.concurrent.duration.FiniteDuration
import scala.reflect.ClassTag

/**
 * A universal type instance allows us to safely convert a value of type [[A]]
 * to a [[Value]] and back. The projection from [[Value]] to [[A]] is not total,
 * so [[project]] returns an [[Option]] type.
 */
sealed abstract class Universal[A] {

  /**
   * Convert a value of type [[A]] to a [[Value]]. Passing the result of this
   * method to any [[project]] other than this exact instance's [[project]] will
   * yield [[None]].
   */
  def inject(value: A): Value[A]

  /**
   * Retrieve the stored [[A]] as a [[Some]] if and only if the passed [[Value]]
   * was created by this instance's [[inject]]. In any other case, this returns
   * [[None]].
   */
  def project(value: Value[_]): Option[A]

}

case object Universal {

  /**
   * Base class for Inject classes in [[embed]]. Subclasses contain the
   * actual values.
   */
  sealed abstract class Value[A]

  /**
   * Create a [[Universal]] instance for a given type. Note that two distinct
   * instances for the same type will produce incompatible [[Value]]s, meaning
   * their [[Universal.project]] method will return [[None]].
   */
  def embed[A](implicit classTag: ClassTag[A]): Universal[A] = new Universal[A] {

    /**
     * The new subclass of [[Value]] containing this [[Universal]] instance's
     * injected values.
     */
    private final case class Inject(value: A) extends Value[A] {
      override def toString: String = value.toString
    }

    override def inject(value: A): Value[A] = Inject(value)
    override def project(value: Value[_]): Option[A] = {
      value match {
        // TODO(iphydf): Why is the ClassTag and the type check here needed?
        case Inject(projected: A) => Some(projected)
        case _                    => None
      }
    }

  }

  /**
   * Some [[Universal]] instances for common Scala types.
   */
  implicit val uniInt: Universal[Int] = Universal.embed
  implicit val uniString: Universal[String] = Universal.embed
  implicit val uniFiniteDuration: Universal[FiniteDuration] = Universal.embed

}
