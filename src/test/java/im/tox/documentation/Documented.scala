package im.tox.documentation

/**
 * Every package object with documentation must inherit from this class.
 */
abstract class Documented {
  /**
   * Create a reference to a Java/Scala type in the documentation.
   * @tparam T The type to print in the documentation.
   */
  protected final def ref[T](implicit evidence: Manifest[T]): Doc = {
    Doc.Ref("[[" + evidence.runtimeClass.getSimpleName + "]]")
  }

  /**
   * Create a reference to a Scala singleton object.
   * @tparam T The type of the object to be printed.
   */
  protected final def ref[T](value: T): Doc = {
    Doc.Ref("[[" + value.getClass.getSimpleName + "]]")
  }

  /**
   * Initialised to just the name of the package object. Any [[DocumentationInterpolator.doc]] calls in the actual
   * package object will override this, but [[name]] will already be set then.
   */
  private var document: Doc = Doc.Ref("[[" + getClass.getName.replace(".package$", "") + "]]")

  /**
   * The name of the package object. Used to link to other package object documentation blocks.
   */
  final val name: Doc = document

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  implicit class DocumentationInterpolator(val sc: StringContext) {
    def doc(args: Doc*): Unit = {
      val packageName = (new Throwable).getStackTrace()(1).getClassName
      val documentation = sc.parts.zip(args.map(_.reference)).map { case (a, b) => a + b }.mkString.stripMargin
      assert(document == name)
      document = Doc.Text(packageName, documentation)
    }
  }

  final override def toString: String = document.toString
}
