package im.tox.tox4j.lint

import org.brianmckenna.wartremover.{WartTraverser, WartUniverse}

/**
 * Checks that [[Option.orNull]] is not used. This method is useful for Java
 * interoperability, but is generally unsafe, as it produces potentially null
 * values, giving the system opportunity to throw a [[NullPointerException]].
 *
 * Code that interoperates with Java code will need to use [[SuppressWarnings]].
 */
@SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Any"))
object OptionOrNull extends WartTraverser {

  val errorMessage = "Option#orNull is disabled"

  private final case class Checker[U <: WartUniverse](u: U) {

    final class Make extends u.universe.Traverser {

      import u.universe._

      val optionSymbol = rootMirror.staticClass(classOf[Option[_]].getName)
      val OrNullName = TermName("orNull")

      override def traverse(tree: Tree): Unit = {
        tree match {
          // Ignore trees marked by @SuppressWarnings
          case t: Tree if hasWartAnnotation(u)(t) =>
          // TODO(iphydf): how is this reached?
          // case LabelDef(_, _, rhs) if isSynthetic(u)(tree) =>

          case Select(left, OrNullName) if left.tpe.baseType(optionSymbol) != NoType =>
            u.error(tree.pos, errorMessage)

          case _ => super.traverse(tree)
        }
      }

    }

  }

  def apply(u: WartUniverse): u.universe.Traverser = {
    val factory = Checker[u.type](u)
    new factory.Make
  }

}
