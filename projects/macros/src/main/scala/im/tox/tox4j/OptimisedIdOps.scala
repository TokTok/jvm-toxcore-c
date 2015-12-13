package im.tox.tox4j

import scala.annotation.compileTimeOnly
import scala.language.experimental.macros
import scala.language.{existentials, implicitConversions}
import scala.reflect.macros.whitebox
import scala.util.Random

/**
 * A wrapper class for simple operations implemented as macros.
 */
final case class OptimisedIdOps[A](self: A) extends AnyVal {

  /**
   * Reverse-apply operator.
   *
   * x |> f == f(x)
   *
   * This macro also supports x |> (x => body). This allows for reuse of names in a series of
   * functional updates. E.g.:
   *
   * (state
   *   |> (state => do something to state)
   *   |> (state => do something to the last state)
   *   |> (state => another thing using the most recent state))
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
    // $COVERAGE-OFF$
    throw new RuntimeException(s"$OptimisedIdOps was not optimised away")
    // $COVERAGE-ON$
  }

  private final val random = new Random()

  private final case class MakeTree[C <: whitebox.Context](c: C) {

    import c.universe._

    def apply(f: Tree): Tree = {
      // Unwrap the "a" argument from the toOptimisedIdOps call.
      val unwrappedSelf = c.prefix.tree match {
        case Apply(TypeApply(conversion, _), List(self)) => self
      }

      // Unwrap the function from the generated lambda.
      f match {
        // If it's of the form ((x: A) => f(x)), transform it to
        // f(a).
        case Block(_,
          Function(
            List(ValDef(_, TermName(argDecl), _, _)),
            Apply(wrappedFunction, List(Ident(TermName(argUse))))
            )
          ) if argDecl == argUse =>
          q"$wrappedFunction($unwrappedSelf)"

        // This transforms the remainder of the direct lambdas of the form a |> ((a: A) => body)
        // to { val $tmp = a; { val a = $tmp; body } }.
        case Function(List(ValDef(_, name, ty, _)), body) =>
          val tmpName = TermName("$id_ops_tmp_" + random.nextInt())

          val tmpDecl = ValDef(Modifiers(), tmpName, ty, unwrappedSelf)
          val argDecl = ValDef(Modifiers(), name, ty, Ident(tmpName))

          val reparsedBody = c.parse(showCode(body))

          q"{ $tmpDecl; { $argDecl; $reparsedBody } }"

        case wrappedFunction =>
          q"$wrappedFunction($unwrappedSelf)"
      }
    }

  }

  def reverseApplyImpl[A, B](c: whitebox.Context)(f: c.Expr[A => B]): c.Expr[B] = {
    c.Expr[B](MakeTree[c.type](c)(f.tree))
  }

}
