package im.tox.tox4j.lint

import org.brianmckenna.wartremover.{WartTraverser, WartUniverse}

/**
 * Specialised checker for specific coding conventions in tox4j options packages. Options classes are
 * used for constructor arguments to ToxCore and ToxAv instances.
 *
 * This checker verifies:
 * - Options classes are all case classes.
 * - All constructor parameters have default arguments.
 * - Except if the class is part of an ADT (such as ProxyOptions.Type). In that case,
 *   it does not need to have default arguments, because a None-value (e.g. ProxyOptions.None)
 *   is provided.
 * - There is no inheritance outside of the options packages. Options classes can not, for
 *   example, inherit from collection types. Options classes are meant to be trivial data
 *   carriers.
 */
@SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Any"))
object ToxOptionsClasses extends WartTraverser {

  def errorDefaultArguments: String = "Non-ADT options case class must have default arguments for all parameters"
  def errorCaseClass: String = "Options classes must be case classes"
  def errorBadParent(parentName: String): String = s"Options classes can only inherit from options traits: $parentName not allowed"

  private final case class Checker[U <: WartUniverse](u: U) {

    final class Make extends u.universe.Traverser {

      import u.universe._

      /**
       * Checks whether a symbol is defined in one of the tox4j options packages (core and av).
       */
      private def isTox4jOptionsType(symbolName: String): Boolean = {
        Seq("av", "core") exists { pkg =>
          symbolName.startsWith(s"im.tox.tox4j.$pkg.options")
        }
      }

      /**
       * Checks whether one of the types is defined in the tox4j options package.
       */
      private def containsTox4jOptionsType(types: List[u.universe.Tree]): Boolean = {
        types.exists { parent =>
          isTox4jOptionsType(parent.tpe.typeSymbol.fullName)
        }
      }

      /**
       * Extract the first and only parameters list from a constructor.
       *
       * This function returns [[Nil]] if
       * - the Tree passed is not a DefDef,
       * - the DefDef passed is not a constructor, or
       * - the constructor has more than one parameter list.
       *
       * @return A possibly empty list of constructor parameters.
       */
      private def getConstructorParams(tree: u.universe.Tree): List[u.universe.ValDef] = {
        tree match {
          case DefDef(mods, termNames.CONSTRUCTOR, tparams, List(vparams), tpt, rhs) =>
            vparams
          case _ =>
            Nil
        }
      }

      /**
       * Returns a list of positions where a constructor parameter of a class does not have a default argument.
       *
       * @param body The class body containing all the methods, including constructors.
       */
      private def filterNoDefaultArguments(body: List[u.universe.Tree]) = {
        for {
          decl <- body
          param <- getConstructorParams(decl)
          if param.rhs.isEmpty
        } yield {
          param.pos
        }
      }

      /**
       * Return a list of position/name pairs of unacceptable supertypes. Acceptable supertypes are only the
       * ones that every Scala case class shares (Any, Product, Serializable) and other types defined in the
       * options packages.
       *
       * @param parents A list of type symbols representing the supertypes.
       */
      private def filterBadParents(parents: List[u.universe.Tree]): List[(u.universe.Position, String)] = {
        val allowedParents = Seq(
          "java.lang.Object",
          "scala.Product",
          "scala.Serializable"
        )

        for {
          (pos, parentName) <- parents map { parent =>
            (parent.pos, parent.tpe.typeSymbol.fullName)
          }
          if !isTox4jOptionsType(parentName) && !allowedParents.contains(parentName)
        } yield {
          (pos, parentName)
        }
      }

      private def checkTree(tree: u.universe.Tree) = {
        tree match {
          case ClassDef(mods, typeName, _, Template(parents, self, body)) if isTox4jOptionsType(tree.symbol.fullName) =>
            filterBadParents(parents) foreach {
              case (pos, parentName) =>
                u.error(pos, errorBadParent(parentName))
            }

            if (mods.hasFlag(Flag.CASE)) {
              if (!containsTox4jOptionsType(parents)) {
                filterNoDefaultArguments(body) foreach { pos =>
                  u.error(pos, errorDefaultArguments)
                }
              }
            } else if (!mods.hasFlag(Flag.TRAIT)) {
              u.error(tree.pos, errorCaseClass)
            }

          case _ =>
        }
      }

      override def traverse(tree: u.universe.Tree): Unit = {
        checkTree(tree)
        super.traverse(tree)
      }

    }

  }

  def apply(u: WartUniverse): u.universe.Traverser = {
    val factory = Checker[u.type](u)
    new factory.Make
  }

}
