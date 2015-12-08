package im.tox.tox4j

import scala.annotation.compileTimeOnly
import scala.language.experimental.macros
import scala.language.{existentials, implicitConversions}
import scala.reflect.macros.whitebox

/**
 * A wrapper class for simple operations implemented as macros.
 */
final case class OptimisedIdOps[A](self: A) extends AnyVal {

  /**
   * Reverse-apply operator.
   *
   * x |> f == f(x)
   */
  def |>[B](f: A => B): B = macro OptimisedIdOps.reverseApplyImpl[A, B] // scalastyle:ignore method.name

}

@SuppressWarnings(Array(
  "org.brianmckenna.wartremover.warts.Any",
  "org.brianmckenna.wartremover.warts.AsInstanceOf"
))
object OptimisedIdOps {

  @compileTimeOnly("OptimisedIdOps was not optimised away")
  implicit def toOptimisedIdOps[A](a: A): OptimisedIdOps[A] = {
    throw new RuntimeException(s"$OptimisedIdOps was not optimised away")
  }

  private final case class MakeTree[C <: whitebox.Context](c: C) {

    import c.universe._

    def apply(f: Tree): Tree = {
      // Unwrap the "a" argument from the toOptimisedIdOps call.
      val unwrappedSelf = c.prefix.tree match {
        case Apply(TypeApply(conversion, _), List(self)) => self
      }

      // Unwrap the function from the generated lambda.
      val unwrappedFunction = f match {
        // Only if it's of the form ((x: T) => f(x)).
        // For example, ((x: Int) => x + 1) can not be optimised.
        case Block(_,
          Function(
            List(ValDef(_, TermName(argDecl), _, _)),
            Apply(wrappedFunction, List(Ident(TermName(argUse))))
            )
          ) if argDecl == argUse =>
          wrappedFunction

        case wrappedFunction =>
          wrappedFunction
      }

      q"$unwrappedFunction($unwrappedSelf)"
    }

  }

  def reverseApplyImpl[A, B](c: whitebox.Context)(f: c.Expr[A => B]): c.Expr[B] = {
    c.Expr[B](MakeTree[c.type](c)(f.tree))
  }

}
