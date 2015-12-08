package im.tox.tox4j

import scala.collection.immutable.TreeSet
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

/**
 * A macro to produce a TreeSet of all instances of a sealed trait.
 * Based on Travis Brown's work: http://stackoverflow.com/questions/13671734/iteration-over-a-sealed-trait-in-scala
 */
@SuppressWarnings(Array(
  "org.brianmckenna.wartremover.warts.Any",
  "org.brianmckenna.wartremover.warts.AsInstanceOf"
))
object EnumerationMacros {

  def sealedInstancesOf[A]: TreeSet[A] = macro sealedInstancesOfImpl[A]

  private final case class MakeTree[C <: blackbox.Context](c: C) {

    import c.universe._

    private def sourceModuleRef(sym: Symbol): Ident = {
      Ident(sym.asInstanceOf[scala.reflect.internal.Symbols#Symbol].sourceModule.asInstanceOf[Symbol])
    }

    def apply[A](symbol: ClassSymbol): Tree = {
      if (!symbol.isClass || !symbol.isSealed) {
        c.abort(c.enclosingPosition, "Can only enumerate values of a sealed trait or class.")
      } else {
        val children = symbol.knownDirectSubclasses.toList

        if (!children.forall(_.isModuleClass)) {
          c.abort(c.enclosingPosition, "All children must be objects.")
        } else {
          Apply(
            Select(
              reify(TreeSet).tree,
              TermName("apply")
            ),
            children.map(sourceModuleRef)
          )
        }
      }
    }

  }

  def sealedInstancesOfImpl[A: c.WeakTypeTag](c: blackbox.Context): c.Expr[TreeSet[A]] = {
    import c.universe._

    c.Expr[TreeSet[A]] {
      MakeTree[c.type](c)(weakTypeOf[A].typeSymbol.asClass)
    }
  }

}