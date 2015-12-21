package im.tox.tox4j.lint

import org.brianmckenna.wartremover.{WartTraverser, WartUniverse}

import scala.annotation.tailrec

/**
 * Checks that every method that has a superclass method it overrides, even if
 * that method is abstract, has the 'override' qualifier.
 *
 * The Scala language requires 'override' only for methods that actually override
 * an implementation. We also require implementations to say 'override'. The
 * rationale for this is that this makes it immediately clear that the method is
 * part of an implemented interface. It also prevents the situation where an
 * interface method used to exist, was implemented, and then removed from the
 * interface, leaving the implementation lingering.
 */
@SuppressWarnings(Array(
  "org.brianmckenna.wartremover.warts.Any",
  "org.brianmckenna.wartremover.warts.ToString"
))
object Override extends WartTraverser {

  def errorMessage(name: String): String = "Implementations of abstract methods must have the 'override' modifier: " + name
  def warningOverloads(name: String): String = "Overloaded versions exist; cannot decide whether it needs 'override': " + name

  private final case class Checker[U <: WartUniverse](u: U) {

    final class Make extends u.universe.Traverser {

      import u.universe._

      /**
       * Returns whether the method needs the 'override' qualifier.
       *
       * @param method The current method under analysis.
       * @param baseClasses The list of direct or indirect base classes.
       */
      private def needsOverride(method: DefDef, baseClasses: Set[Type]): Boolean = {
        !isSynthetic(u)(method) &&
          method.name != termNames.CONSTRUCTOR &&
          method.name != TermName("$init$") &&
          method.name != TermName("isDefinedAt") &&
          !method.mods.hasFlag(Flag.OVERRIDE) &&
          !method.mods.hasFlag(Flag.CASEACCESSOR) &&
          baseClasses.exists { base =>
            val baseSymbol = base.decl(method.name)
            if (baseSymbol.alternatives.length > 1) {
              u.warning(method.pos, warningOverloads(method.name.toString))
              false
            } else {
              baseSymbol != NoSymbol
            }
          }
      }

      /**
       * Recursively find all base types of a set of types.
       *
       * @param types A set of types.
       * @return The original types plus all their base types.
       */
      @tailrec
      private def recursiveBaseClasses(types: Set[Type]): Set[Type] = {
        val baseTypes = types.flatMap(tpe => tpe.baseClasses.map(base => tpe.baseType(base)))
        if (baseTypes.subsetOf(types)) {
          // No more types to find.
          types
        } else {
          recursiveBaseClasses(types ++ baseTypes)
        }
      }

      var baseClasses: Set[Type] = Set.empty // scalastyle:ignore var.field

      override def traverse(tree: Tree): Unit = {
        tree match {
          case classDef @ ClassDef(mods, name, tparams, Template(parents, self, body)) =>
            // We're entering a new (inner) class, so save the old baseClasses and restore after traverse.
            val oldBaseClasses = baseClasses
            baseClasses = recursiveBaseClasses(parents.map(_.tpe).toSet)
            super.traverse(tree)
            baseClasses = oldBaseClasses
          case method: DefDef if needsOverride(method, baseClasses) =>
            u.error(method.pos, errorMessage(method.name.toString))
            super.traverse(tree)
          case _ =>
            super.traverse(tree)
        }
      }

    }

  }

  def apply(u: WartUniverse): u.universe.Traverser = {
    val factory = Checker[u.type](u)
    new factory.Make
  }

}
